package com.tuoying.hykc.utils;

import com.tuoying.hykc.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

public class Questions {

    public static List<QuestionEntity> getQuestions(){
        return initQuestionDatas();

    }

    private static List<QuestionEntity> initQuestionDatas(){
        List<QuestionEntity> list=new ArrayList<>();
        QuestionEntity q1=new QuestionEntity("货运驾驶员运输过程中应避免“法不责众”的想法，克服随众心理，文明驾驶。","Y",false);
        list.add(q1);
        QuestionEntity q2=new QuestionEntity("货运驾驶员在运输过程中应谨慎驾驶，提前预见危险，切忌急躁心理。","Y",false);
        list.add(q2);
        QuestionEntity q3=new QuestionEntity("运输过程中感到沾沾自喜时应及时自省，提醒自己集中注意力驾驶，安全第一。","Y",false);
        list.add(q3);
        QuestionEntity q4=new QuestionEntity("货运驾驶员要培养自己的好胜心理，这样才能尽快将货物送达目的地。","N",false);
        list.add(q4);
        QuestionEntity q5=new QuestionEntity("运输过程中遇别人占道行驶时，要心平气和、宽容忍让。","Y",false);
        list.add(q5);
        QuestionEntity q6=new QuestionEntity("运输过程中遇到别人争道抢行时，要和他一较高下，教训他的不文明行为。","N",false);
        list.add(q6);
        QuestionEntity q7=new QuestionEntity("运输过程中应时刻提醒自己谨慎行车，避免麻痹大意心理。","Y",false);
        list.add(q7);
        QuestionEntity q8=new QuestionEntity("运输过程中感到疲劳时，应加速行驶，尽快到达目的地后休息。","N",false);
        list.add(q8);
        QuestionEntity q9=new QuestionEntity("运输过程中要遵守操作规程和交通法规，避免侥幸心理。","Y",false);
        list.add(q9);
        QuestionEntity q10=new QuestionEntity("货运驾驶员运输过程中应避免“法不责众”的想法，克服随众心理，文明驾驶。","Y",false);
        list.add(q10);
        QuestionEntity q11=new QuestionEntity("《安全生产法》规定，生产经营单位可以以任何形式与从业人员订立劳动合同，合同内容由生产经营单位决定。","N",false);
        list.add(q11);
        QuestionEntity q12=new QuestionEntity("《安全生产法》规定，生产经营单位的从业人员发现直接危及人身安全的紧急情况时，有权在采取可能的应急措施后撤离作业场所。","Y",false);
        list.add(q12);
        QuestionEntity q13=new QuestionEntity("《安全生产法》规定，生产经营单位的从业人员发现直接危及人身安全的紧急情况时，无权停止作业。","N",false);
        list.add(q13);
        QuestionEntity q14=new QuestionEntity("《道路交通安全法》规定，登记后上道路行驶的机动车，要依照法律法规规定，不定期进行安全技术检验。","N",false);
        list.add(q14);

        return list;
    }

}
