package io.alcatraz.audiohq.beans;

public class QueryElement
{
    private String author;
    private String name;
    private String url;
    private String license;
    private String intro;
    public void setAuthor(String au){
        author=au;
    }
    public void setName(String n){
        name=n;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setLicense(String license){
        this.license=license;
    }
    public void setintro(String intro){
        this.intro=intro;
    }
    public String getAuthor(){
        return author;
    }
    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public String getLicense(){
        return license;
    }
    public String getIntro(){
        return intro;
    }
}