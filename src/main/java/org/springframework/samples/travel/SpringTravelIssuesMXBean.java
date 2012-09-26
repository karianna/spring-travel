package org.springframework.samples.travel;

public interface SpringTravelIssuesMXBean {

    public static final String OBJECT_NAME = "com.jclarity:type=Problems,name=problem";

    boolean getInefficientQuery();

    void setInefficientQuery(boolean enable);

    boolean getPointlessWrite();

    void setPointlessWrite(boolean enable);

    boolean getEvenLessEfficientQuery();

    void setEvenLessEfficientQuery(boolean enable);

}
