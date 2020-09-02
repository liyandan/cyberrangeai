package com.troila.cyberrangeai.controller;

import com.troila.cyberrangeai.exception.AiException;
import com.troila.cyberrangeai.heartbeat.ReceivedMail;
import com.troila.cyberrangeai.pojo.Capibility;
import com.troila.cyberrangeai.pojo.EmailTo;
import com.troila.cyberrangeai.pojo.SurfingUrls;
import com.troila.cyberrangeai.requestbody.CapabilityBody;
import com.troila.cyberrangeai.requestbody.DecisionBody;
import com.troila.cyberrangeai.service.*;
import com.troila.cyberrangeai.utils.ReplyEmail;
import com.troila.cyberrangeai.utils.range;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.lang.Math.E;


@RestController
@RequestMapping("/api/v1")
public class cyberrangeaicontroller {

    //使用SLF4作为日志上层抽象框架
    Logger logger = LoggerFactory.getLogger(getClass());

    //引入userService
    @Autowired
    private UserService         userService;
    @Autowired
    private EmailtoService      emailtoService;
    @Autowired
    private SurfingurlService   surfingurlService;
    @Autowired
    private CapibilityService   capibilityService;

    //暂时采静态资源存放语料，后边会更新
    private final static  String[] SubjectCandidates = {"这一家人怎么回事啊？",
                                                        "没语言啊没语言",
                                                        "回头率说明不了问题"};
    private final static String[]  BodyCandidates    = {"朋友喜欢上一个老男人,一想到他就激动得不得了，可是他对她说他过了那个卿卿我我的年龄了，然后让她考博士再说，这个，要知道，考上了，就得去读了。",
                                                        "我跟他应该算是不错的朋友关系吧！在和他某一次下象棋以后。她找到了我，一问，才知道原来她十分爱他。为了攒点RP，我竭力去帮她。我问他，对这女孩感觉怎么样，他说没感觉，一点都不爱她，相识真是孽缘。我觉得这女孩真可怜，想安慰她，想为她做点什么，一方面两人之间互相了解，在性格方面她和我竟有着惊人的相似。\n" +
                                                                "        5月19日不小心把手机摔碎了，心情极度郁闷，当晚打电话给她，决定次日去西安调节自己的心情。\n" +
                                                                "        次日两人见了面，从网络中走到了现实。在西安这三天两人过的十分愉快，该吃的都吃了，该去的地方都去了，两人彼此对对方的感觉还不错，觉得都很幸福。于是走到了一起。也有了共同的约定。我以为我找到了自己的爱情，事实却与理想相逆。",
                                                        "他父母对我也很好，春节去他家，他父母给我买了条金项链，开学回来给我们带了6000块钱，一人3000，。因为这学期要毕业，忙着找工作，事情多，花了不少钱，6000块钱没够我俩花，他父母又给我们寄了2000。现在马上要毕业了，因为我欠学校21000学费，我家没钱给我，不交又?: 我有一个姐姐和弟弟，姐姐结婚了，但姐夫没有父母，没有自己的家，他俩在我姐单位有一个挺不错的家属院，但姐姐不太喜欢住在那里，姐夫在家他们就住那里，姐夫出去打工，姐姐就回家住，一年要在家住10月都不止。因为家里方便，妈妈可以伺候她。弟弟大学毕业工作了，单位"};

    //以下是对AI用户的状态维护变量：邮件回复超时窗口、邮件缓存数量、接收到的邮件状态（接收时间、是否被回复、回复内容）
    //AI用户在特定网络场景的最后登录时间维护
    //这是个超时时间窗口，超过7天的邮件就不需要回复了
    private final static int              MaxTimeInterval = 86400 * 7;

    //这是个缓存邮件的最大配置，目前的策略，当达到最大限制就直接将现有缓存清空到一半
    private final static int              MaxCacheNum     = 3000;

    //每个场景下的用户维护一个账户缓存 作为参数读取， 时间段也作为参数读取(线程安全)
    private Map<String, ArrayList<ReceivedMail> >  userReceivedMailCache = new Hashtable<String,ArrayList<ReceivedMail> >();
    //记录每个用户在对应网络场景下的状态：0-登录；1-上网中；2-未登录 ....
    private Map<String,Long>                       userLogonSession      = new Hashtable<String,Long>();

    //更新该用户在此网络场景的用户登入时间
    private boolean updateUserLogonTime(String UserId,String ScenarioId)
    {
        try {
              String id = UserId.trim() + ScenarioId.trim();
              Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
              userLogonSession.put(id,now);
        }catch (Exception e){
              logger.error(e.getCause().toString());
              return false;
        }
        return true;
    }
    //判断这个用户在这个场景下是否还处于登录状态
    private boolean isLogonStat(String UserId,String ScenarioId,long maxInterval){
        //已经存在的用户,查看是否超时登出
        try {
            String id = UserId.trim() + ScenarioId.trim();
            Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
            if (userLogonSession.containsKey(id)){
                if( now.longValue() -  userLogonSession.get(id).longValue() <= maxInterval ){
                    return true;
                }
            }
        }catch (Exception e){
            logger.error(e.getCause().toString());
            return false;
        }
        return false;
    }

    private ReplyEmail getRandomEmail(String toMailAddress){
        try {
            ReplyEmail randomReply = new ReplyEmail();
            randomReply.setTo(toMailAddress);
            randomReply.setCc("");
            //目前的实现是从静态的数组资源中随机取出个标题
            randomReply.setSubject(SubjectCandidates[new Random().nextInt(3)]);
            //目前的实现是从静态的数组资源中随机取出个邮件正文内容
            randomReply.setBody(BodyCandidates[new Random().nextInt(3)]);
            return randomReply;
        }catch (Exception e){
             logger.error(e.getCause().toString());
             return null;
        }
    }

    //从跟这个userId 和 scenarioId关联的接收到的邮件中随机选取一封，生成邮件返回结果
    private ReplyEmail getReplyEmail(String id){

        ArrayList<ReceivedMail> rcvdMails = userReceivedMailCache.get(id);
        if(null == rcvdMails){
            return null;
        }else{

            //遍历所有未回复的邮件,得到未回复邮件的个数，生成随机数，确定回复的邮件
            int num_rcvd = rcvdMails.size();
            int num_unreply = 0;
            for(int i = 0; i < num_rcvd;++i){
                if(rcvdMails.get(i).getStat() == 0){
                    num_unreply++;
                }
            }

            if( num_unreply > 0 ) {
                Random ndm = new Random();
                int selectedmailIdx = ndm.nextInt(num_unreply);

                int selIdx = 0;
                int nRealSelIdx = 0;
                for(int i = 0; i < num_rcvd;++i){
                    if(rcvdMails.get(i).getStat() == 0){
                        selIdx++;
                        if(selIdx >= selectedmailIdx){
                            nRealSelIdx = i;
                            break;
                        }
                    }
                }

                ReceivedMail rcvdMail = rcvdMails.get(nRealSelIdx);

                //根据内容生成回复邮件
                ReplyEmail rplyMail = generateReplyEmail(rcvdMail);
                userReceivedMailCache.get(id).get(nRealSelIdx).setStat(1);
                return rplyMail;
            }else{
                //查询哪些邮箱可以发送，从中随机选择一个，生成邮件内容
                List<EmailTo> toSenMails = new ArrayList<EmailTo>();

                //从该用户的可发送邮件库中将所有可发送邮箱地址查询出来，缓存到本地
                Iterator<EmailTo> emailToIterator = emailtoService.getAllById(id).iterator();
                while( emailToIterator.hasNext() ){
                    EmailTo item = emailToIterator.next();
                    toSenMails.add(item);
                    logger.info("该用户可以发邮件给: [ " + item.getFullname() + " ] ; 邮箱地址 : [ " + item.getEmail() + " ]") ;
                }

                String sendMailSelected = "";
                if(toSenMails.size() > 0){  //从可发送列表中选取一个
                    int idx = new Random().nextInt(toSenMails.size() );
                    sendMailSelected = toSenMails.get(idx).getEmail();

                }else if (rcvdMails.size() > 0 ){
                    //从缓存收到的邮件中选一个
                    int rcdmailNum = rcvdMails.size();
                    int idx = new Random().nextInt(rcdmailNum);
                    sendMailSelected = rcvdMails.get(idx).getFrom();
                }

                if(sendMailSelected.length() == 0){
                    return null;
                }

                //根据选中的邮件地址随机生成些数据
                return getRandomEmail(sendMailSelected);
            }
        }

    }

    //利用算法生成回复邮件
    private ReplyEmail generateReplyEmail(ReceivedMail mail){
        return generateReplyEmail(mail.getFrom(),mail.getCc(),mail.getSubject(),mail.getBody());
    }

    //算法生成回复邮件内容：标题、正文，先根据标题和内容生成一个版本，再考虑优化问题
    private ReplyEmail generateReplyEmail(String toMail,String ccMails,String subject,String body){
        //TODO 根据接收到的邮件生成回复内容
        ReplyEmail  replymail = new ReplyEmail();
        replymail.setTo(toMail);
        replymail.setCc(ccMails);
        replymail.setSubject("回复:" + subject);

        ////////////////////////////////////////////////////////////////
        //TODO 根据body内容生成回复内容，也就是对话内容，当前的做法是直接从语
        //     料库中找到和body最相似的文本回复出去
        //TODO 1 加载语料库
        //TODO 2 将原文body和问题进行匹配
        //TODO 3 找到对应的答案
        ////////////////////////////////////////////////////////////////

        //当前的做法是直接将"回复:"这样的字样去掉，然后增加"" 放到body的头
        String replyBody = body;
        replyBody.replace("回复:","");
        replymail.setBody(replyBody);

        return replymail;
    }

    //选择一个浏览的网页
    private String getSurfUrl( List<String>  urls ){

         //说明：先给一个随机url，然后再按照算法和url关联关系选取
        try {
            int num = urls.size();
            if(num == 0){
                return "";
            }
            Random random = new Random();
            int idx  = random.nextInt( num );
            return urls.get(idx).toString();

        }catch(Exception e){
            logger.error(e.getCause().toString());
            return "";
        }
    }

    //根据id( userId+scenarioId )查询所有可以访问的url，然后随机从中选择一个url
    private String getSurUrl(String id){

        //首先按照id从数据库中查询到所有的url，然后再在这堆url中进行悬则
        String url                         = "";
        List<String> urls                  = new ArrayList<String>();

        //获取到跟这个用户和这个网络场景 相有关联的所有浏览的url
        Iterator<SurfingUrls> urlsIterator = surfingurlService.getAllById(id).iterator();
        while(urlsIterator.hasNext()){

            SurfingUrls item = urlsIterator.next();
            urls.add(item.getUrl());
            logger.info("searhed  id = [ " + item.getId() + " ]; " + "url: [ " + item.getUrl() + " ]");
        }

        url = getSurfUrl(urls);
        logger.info("Selected url : [ " + url + " ]");

        return url;
    }

    //从算法模块中得到本次决策返回
    private String getDecision(String id){
        try {
            String decisionAction = "";
            //以下是跟这个场景下该用户的关联关系数据库查询操作
            Map<String, Float> capibilityMap        = new Hashtable<String, Float>();
            Map<String, range> capibilityRangeMap   = new Hashtable<String, range>();
            Map<String, Float> probabilityMap       = new Hashtable<String, Float>();

            //获取到跟这个用户和这个网络场景相有关联的行为能力和权重占比
            Iterator<Capibility> capibilitiesIterator = capibilityService.getAllById(id).iterator();
            while (capibilitiesIterator.hasNext()) {
                Capibility item = capibilitiesIterator.next();
                capibilityMap.put(item.getAction().toString(), new Float(Float.parseFloat(item.getWeight())));
                logger.info("该人工智能用户所具有的行为能力有：[ " + item.getAction() + " ] ; 权重: [ " + item.getWeight() + " ]");
            }

            //计算每个action的概率占比，然后按照这个概率生成事件。
            double total = 0.0;
            for (Map.Entry<String, Float> entry : capibilityMap.entrySet()) {
                String mapKey = entry.getKey();
                Float mapValue = entry.getValue();
                total += Math.pow(E, mapValue.floatValue());
            }
            for (Map.Entry<String, Float> entry : capibilityMap.entrySet()) {
                String mapKey = entry.getKey();
                Float mapValue = entry.getValue();
                float value = (float) (Math.pow(E, mapValue.floatValue()) / total);
                probabilityMap.put(mapKey, new Float(value));
            }

            //以10000为种子
            int idx = 0;
            for (Map.Entry<String, Float> entry : probabilityMap.entrySet()) {
                String mapKey = entry.getKey();
                Float mapValue = entry.getValue();
                int nWindow = (int) (10000 * (mapValue.floatValue()));
                range _range = new range();
                _range.setLow(idx);
                _range.setHigh(idx + nWindow);
                idx += nWindow;
                capibilityRangeMap.put(mapKey, _range);
            }

            Random rand = new Random();
            int num = rand.nextInt(10000);

            //通过如下计算选出决策动作
            for (Map.Entry<String, range> entry : capibilityRangeMap.entrySet()) {
                String mapKey = entry.getKey();
                range mapValue = entry.getValue();
                int low = mapValue.getLow();
                int high = mapValue.getHigh();
                if (num >= low && num < high) {
                    decisionAction = mapKey;
                    break;
                }
            }
            return decisionAction;
        }catch(Exception e){
            logger.error(e.getCause().toString());
            return "";
        }
    }


    private void removeCachedMails(){

        int nRemoved = 0;
        int nLeft    = 0;
        for (Map.Entry<String, ArrayList<ReceivedMail>> entry : userReceivedMailCache.entrySet()) {
            String mapKey = entry.getKey();
            ArrayList<ReceivedMail> mapValue = entry.getValue();
            //正确 可删除多个
            Iterator<ReceivedMail> iterator = mapValue.iterator();
            while (iterator.hasNext()) {
                ReceivedMail s = iterator.next();
                if (s.getStat() == 1) {  //已回复的要删除
                    iterator.remove();//使用迭代器的删除方法删除

                }else if(   LocalDateTime.now().toEpochSecond( ZoneOffset.of("+8") )  -   s.getEventOccurTime().longValue()
                              >= MaxTimeInterval)  //超过7天的邮件必须删除
                {
                    iterator.remove();//使用迭代器的删除方法删除
                } else{
                    nLeft++;
                }
            }
        }

        if( nLeft > MaxCacheNum /2 ){
            //继续删除，直到删除剩余元素为原来的一半为止
            int nNeedRemove = nLeft - MaxCacheNum /2;
            int nRemove     = 0;
            int flag        = 0;
            for (Map.Entry<String, ArrayList<ReceivedMail>> entry : userReceivedMailCache.entrySet()) {
                String mapKey = entry.getKey();
                ArrayList<ReceivedMail> mapValue = entry.getValue();
                //正确 可删除多个
                Iterator<ReceivedMail> iterator = mapValue.iterator();
                while (iterator.hasNext()) {
                    iterator.remove();//使用迭代器的删除方法删除
                    nRemove ++;
                    if(nRemove >= nNeedRemove){
                        flag = 1;
                        break;
                    }
                }
                if(1 == flag){
                    break;
                }
            }
        }
    }

    //访问示例:  http://localhost:8080/api/v1/capability
    @ApiOperation(value = "AI用户上报能力" ,  notes="由网络靶场端上报AI用户能力")
    @PostMapping("/capability")
    @ResponseBody
    public String capability(
            @RequestHeader(value="Content-Type",required=true,defaultValue="application/json") String header,
            @RequestBody CapabilityBody capability
    ) throws AiException{


        JSONObject ret = new JSONObject();
        try {
            ret.put("success", false);
            ret.put("status_code", 500);
        }catch(Exception e){
            logger.error("Exception occured : [ " + e.getCause() + " ]");
            //抛出自定义的异常信息
            throw new AiException( e.getCause().toString() );
        }
        JSONObject result = new JSONObject();
        try {

            result.put("header", header);
            result.put("scenario_id", capability.getScenario_id());
            result.put("user_id", capability.getUser_id());
            result.put("user_email", capability.getUser_email());

            logger.info("AI用户能力上报的 user_id = [ " + capability.getUser_id() + " ] scenario_id = [ "
                       + capability.getScenario_id() + " ]  user_email = [ " + capability.getUser_email() + " ] ");


            String userId     = capability.getUser_id().trim();
            String scenarioId = capability.getScenario_id().trim();
            String userEmail  = capability.getUser_email().trim();
            String id         = userId +scenarioId;


            //目前的策略是整体更新，不进行局部的更新
            surfingurlService.deleteAllById( id );
            capibilityService.deleteAllById(id);
            emailtoService.deleteAllById(id);

            //最后删除user
            userService.delete(userId,scenarioId);

            //以下才是真正的更新操作开始
            //根据用户ID和场景ID更新 User表
            userService.update(userId,scenarioId,userEmail);

            //处理capibilities
            int cap_num = capability.getCapibilities().size();
            JSONArray capibilities = new JSONArray();

            for(int i = 0; i < cap_num;i++)
            {
                JSONObject capibility_item = new JSONObject();
                String action = capability.getCapibilities().get(i).getAction().trim();
                float  weight = capability.getCapibilities().get(i).getWeight();
                capibility_item.put("action",action);
                capibility_item.put("weight",String.valueOf(weight));
                capibilities.put(capibility_item);

                //更新能力表
                try {
                    capibilityService.update(id, action, weight);
                }catch(Exception e){
                    logger.error("更新能力表条目失败！ id = [ " + id + " ];action= [ " + action + " ]; weight = [ " + weight + " ]");
                    logger.error(e.getCause().toString());
                }
            }

            result.put("capibilities",capibilities);

            JSONArray surfing_urls = new JSONArray();

            //处理surfing_url_list
            int surfing_url_num = capability.getSurfing_url_list().size();
            for(int i = 0; i < surfing_url_num;i++)
            {
                JSONObject url = new JSONObject();
                String urlitem = capability.getSurfing_url_list().get(i).getUrl().trim();
                url.put("url",urlitem);
                surfing_urls.put(url);

                //更新浏览网页表
                try{
                surfingurlService.update(id,urlitem);
                }catch(Exception e){
                    logger.error("更新浏览网页表条目失败！ id = [ " + id + " ];url = [ " + urlitem + " ]");
                    logger.error(e.getCause().toString());
                }
            }
            result.put("surfing_url_list",surfing_urls);

            //处理email_to_list
            JSONArray  email_tos = new JSONArray();
            int email_to_num = capability.getEmail_to_list().size();
            for(int i = 0;i < email_to_num;i++)
            {
                JSONObject email_to = new JSONObject();

                String fullname = capability.getEmail_to_list().get(i).getFullname().trim();
                String email    = capability.getEmail_to_list().get(i).getMailaccount().trim();
                email_to.put("fullname",fullname);
                email_to.put("email",email);
                email_tos.put(email_to);

                //更新 可以发送的邮件列表
                try {
                    emailtoService.update(id, fullname, email);
                }catch(Exception e){
                logger.error("更新可发送邮件地址表条目失败！ id = [ " + id + " ];email address = [ " + email + " ]; fullname = [ " + fullname + " ]" );
                logger.error(e.getCause().toString());
            }

            }
            result.put("email_to_list",email_tos);

        }catch(Exception e) {
            logger.error("Exception occured : [ " + e.getCause() + " ]");
            //抛出自定义的异常信息
            throw new AiException( e.getCause().toString() );
        }

        logger.info("The input json ==== [  " + result.toString() + "  ]");

        try {
            ret.put("success", true);
            ret.put("status_code", 200);
        }catch(Exception e){
            logger.error("Exception occured : [ " + e.getCause() + " ]");
            //抛出自定义的异常信息
            throw new AiException( e.getCause().toString() );
        }
        return ret.toString();
    }

    //访问示例:  http://localhost:8080/api/v1/decision
    @ApiOperation(value = "AI行为心跳",  notes="由人工智能算法模块接收心跳信息，决定下一步动作")
    @PostMapping("/decision")
    @ResponseBody
    public String decision(
            @RequestHeader(value="Content-Type",required=true,defaultValue="application/json") String header,
            @RequestBody DecisionBody decision
    ) throws AiException {

        //缺省标志决策动作 :idling
        String decisionAction = "idling";

        //id = userId + scenarioId ,跟AI用户和网络场景关联的id
        String globalId       = "";

        //用于构建输入参数的校验JSON
        JSONObject inputJson  = new JSONObject();

        //用于构建返回JSON
        JSONObject outpuJson  = new JSONObject();

        try {
            inputJson.put("header", header);
            inputJson.put("user_id", decision.getUser_id().trim());
            inputJson.put("scenario_id", decision.getScenario_id().trim());

            logger.info("user_id = [ " + decision.getUser_id().trim() +" ]; scenario_id = [ "  + decision.getScenario_id().trim() + " ]");

            String userId     =     decision.getUser_id().trim();
            String scenarioId =     decision.getScenario_id().trim();

            String id         =     userId + scenarioId;
            globalId          =     id;
            Long rcvdStat     =     decision.getStat().longValue();

            logger.info("接收到的心跳包中的 Stat 数值为: [ " + rcvdStat.toString()  + " ]");

            //0-登录；1-上网中；2-未登录 ....
            int  stat         =     rcvdStat.intValue();

            //更新该用户在该网络场景下的心跳接收时间
            updateUserLogonTime(userId,scenarioId);

            logger.info("本次心跳得到的唯一id码为: [ " + id + " ]");

            String algoDecision = getDecision(id);
            if(algoDecision.length() == 0){
                logger.warn("无法通过算法计算出下一步的动作决策");
            }else{
                decisionAction = algoDecision;
            }

            //缓存某个网络场景下一个用户之前的决策动作
            //TODO  暂时还没有时间做这个操作

            //处理 接收到的事件（接收邮件）并进行本地缓存
            int event_num = decision.getEvents().size();
            JSONArray events = new JSONArray();
            Long now = LocalDateTime.now().toEpochSecond( ZoneOffset.of("+8") );

            for (int i = 0; i < event_num; i++) {
                JSONObject event_item = new JSONObject();
                event_item.put("event", decision.getEvents().get(i).getEventname());

                JSONObject data_received_email_obj = new JSONObject();
                data_received_email_obj.put("from", decision.getEvents().get(i).getData_received_email().getFrom());
                data_received_email_obj.put("cc", decision.getEvents().get(i).getData_received_email().getCc());
                data_received_email_obj.put("subject", decision.getEvents().get(i).getData_received_email().getSubject());
                data_received_email_obj.put("body", decision.getEvents().get(i).getData_received_email().getBody());

                System.out.println(data_received_email_obj.toString());
                event_item.put("data_received_email", data_received_email_obj);

                //创建接收邮件缓存
                ReceivedMail rcm = new ReceivedMail();
                rcm.setFrom(decision.getEvents().get(i).getData_received_email().getFrom().trim());
                rcm.setCc(decision.getEvents().get(i).getData_received_email().getCc().trim());
                rcm.setSubject(decision.getEvents().get(i).getData_received_email().getSubject().trim());
                rcm.setBody(decision.getEvents().get(i).getData_received_email().getBody().trim());
                //同一批达到的时间相同
                rcm.setEventOccurTime( now );
                rcm.setStat( 0 );

                //将收到的邮件缓存放入
                if( !userReceivedMailCache.containsKey(id) ){
                    ArrayList<ReceivedMail> cacheMailList = new ArrayList<ReceivedMail>();
                    userReceivedMailCache.put(id,cacheMailList);
                }

                userReceivedMailCache.get(id).add(rcm);


                //按照预设规则清理邮件接收缓存：1、缓存数达到阈值（3000）；
                //                           2、接收到的时间超过阈值（86400*7）

                int nCachedMail = 0;
                for (Map.Entry<String, ArrayList<ReceivedMail>> entry : userReceivedMailCache.entrySet()) {
                    String mapKey = entry.getKey();
                    ArrayList<ReceivedMail> mapValue = entry.getValue();
                    nCachedMail += mapValue.size();
                }
                if(nCachedMail > MaxCacheNum ){
                    //进行一次Cache清理操作
                    removeCachedMails();
                }

                System.out.println(event_item.toString());
                events.put(event_item);
            }
            inputJson.put("events", events);
            inputJson.put("stat", decision.getStat());
            //记录本次心跳的发送端数据
            logger.info("心跳函数的本次输入: ======  " + inputJson.toString() );
        } catch (Exception e) {
            logger.error("Exception occured : [ " + e.getCause() + " ]");
            //抛出自定义的异常信息
            throw new AiException( e.getCause().toString() );
        }

        //决策算法的处理
        try {

            //按照决策结果，构建返回Json串
            outpuJson.put("action", decisionAction.toString());

            //如果决策失误，会在后续判断中将这个状态修改为false
            outpuJson.put("success", true);

            if(decisionAction.equals("send_email")){

                //暂时为模拟,暂时采用随机选取的策略
                ReplyEmail replyGenMail =  getReplyEmail(globalId);

                if(null == replyGenMail){
                     outpuJson.put("success", false);
                }else{
                    JSONObject replyObj = new JSONObject();
                    replyObj.put("to",replyGenMail.getTo()           );
                    replyObj.put("cc",replyGenMail.getCc()           );
                    replyObj.put("subject",replyGenMail.getSubject() );
                    replyObj.put("body",replyGenMail.getBody()       );

                    outpuJson.put("data_send_email",replyObj);
                }

            }else if(decisionAction.equals("surf_internet")){

                //去查询所有可访问的 url
                String selectedlUrl = getSurUrl(globalId);
                if( selectedlUrl.length() == 0 ){
                    //没查到url的情况下，将返回行为模式定为idle
                    decisionAction  = "idle";
                    outpuJson.put("action", decisionAction.toString() );

                    //选取url失败，更新是否成功状态
                    outpuJson.put("success", false);
                }else{
                    JSONObject surfUrlObj = new JSONObject();
                    surfUrlObj.put("url",selectedlUrl.toString() );
                    surfUrlObj.put("repeat",new Random().nextInt(10) );
                    outpuJson.put("data_surf_internet",surfUrlObj);
                }

            }else if(decisionAction.equals("idle")){
                //idle状态的话，仅仅返回这个action，不带具体数据

            }else if(decisionAction.equals("sync_capability")){
               //通知网络靶场重新更新capability，但是目前没办法

            }else{
                outpuJson.put("success",false);
            }

            logger.info("生成的输出结果: [ " + outpuJson.toString() + " ] ");

        } catch (Exception e) {
            logger.error(e.getCause().toString());
            //抛出自定义的异常信息
            throw new AiException( e.getCause().toString() );
        }

        return outpuJson.toString();
    }
}
