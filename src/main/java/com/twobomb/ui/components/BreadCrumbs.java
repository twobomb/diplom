package com.twobomb.ui.components;


import com.twobomb.Utils.AppConst;
import com.twobomb.app.security.SecurityUtils;
import com.twobomb.entity.CourseWork;
import com.twobomb.entity.Discipline;
import com.twobomb.entity.Group;
import com.twobomb.service.DisciplineService;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
@Tag("bread-crumbs")
@HtmlImport("components/BreadCrumbs.html")
public class BreadCrumbs extends PolymerTemplate<BreadCrumbs.Model>{

    DisciplineService disciplineService;

    @Autowired
    public BreadCrumbs(DisciplineService disciplineService) {
        this.disciplineService = disciplineService;
    }

    public static enum ParseIndexes{GROUP,DISCIPLINE,COURSEWORK};

    public void addBreadCrumbs(List<ItemInfo> items){
        getModel().setBreadcrumbsList(items);
    }

    public static Long parseIndex(String indexSegment,ParseIndexes p){
        //group1-discipline1-coursework1
        if(indexSegment == null)
            return null;
        Pattern pattern =  Pattern.compile("(group(\\d+)-?)?(discipline(\\d+)-?)?(coursework(\\d+))?");
        Matcher matcher = pattern.matcher(indexSegment);
        Long indexGroup = null ,indexDiscipline= null, indexCoursework= null;
        if(matcher.matches())
            switch (p){
                case GROUP:
                    pattern = Pattern.compile("group(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexGroup = Long.parseLong(matcher.group(1));
                    if(indexGroup != null && !SecurityUtils.isAccessGranted(Group.class))
                        indexGroup = null;
                    return indexGroup;
                case COURSEWORK:
                    pattern = Pattern.compile("coursework(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexCoursework =  Long.parseLong(matcher.group(1));
                    return indexCoursework;
                case DISCIPLINE:
                    pattern = Pattern.compile("discipline(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexDiscipline =  Long.parseLong(matcher.group(1));
                    return indexDiscipline;
            }
        return null;
    }
    public String textTrimmer(String str){
        if(str.length() > 15)
            return str.substring(0,14)+"...";
        return str;
    }
    public void init(Location location){

        String href = location.getFirstSegment().isEmpty() ? AppConst.PAGE_DEFAULT
                : location.getFirstSegment();
        String indexSegment;
        if(location.getSegments().size() >= 2)
            indexSegment = location.getSegments().get(1);
        else
            indexSegment = "";
        Long indexGroup = parseIndex(indexSegment,ParseIndexes.GROUP);
        Long indexDiscipline = parseIndex(indexSegment,ParseIndexes.DISCIPLINE);
        Long indexCoursework = parseIndex(indexSegment,ParseIndexes.COURSEWORK);

        List<ItemInfo> bc= new ArrayList<>();
        try {
        String groupName = "";
        if(indexGroup != null)
            groupName = " ("+textTrimmer(disciplineService.getClassEntityByIndex(indexGroup,Group.class).getName())+")";
        String disciplineName = "";
        if(indexDiscipline != null)
            disciplineName= " ("+textTrimmer(disciplineService.getClassEntityByIndex(indexDiscipline,Discipline.class).getName())+")";
        String courseworkName = "";
        if(indexCoursework != null)
            courseworkName = " (" + textTrimmer(disciplineService.getClassEntityByIndex(indexCoursework , CourseWork.class).getName())+ ")";

        switch (href){
            case AppConst.PAGE_COURSEWORKS:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    if(indexDiscipline!= null)
                        link+= "/discipline"+indexDiscipline;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS+groupName));
                }
                if(indexDiscipline != null){
                    String link = "/"+AppConst.PAGE_DISCIPLINES;
                    if(indexGroup!= null)
                        link+= "/group"+indexGroup;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES+disciplineName));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_COURSEWORK,AppConst.TITLE_COURSEWORK+courseworkName));
                break;
            case AppConst.PAGE_DISCIPLINES:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS+groupName));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES+disciplineName));
                break;
            case AppConst.PAGE_THEMES:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    if(indexDiscipline!= null) {
                        link += "/discipline" + indexDiscipline;
                        if(indexCoursework!= null)
                            link+= "-coursework"+indexCoursework;
                    }
                    else if(indexCoursework!= null)
                        link+= "/coursework"+indexCoursework;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS+groupName));
                }
                if(indexDiscipline != null){
                    String link = "/"+AppConst.PAGE_DISCIPLINES;
                    if(indexGroup!= null)
                        link+= "/group"+indexGroup;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES+disciplineName));
                }

                if(indexCoursework != null){
                    String link = "/"+AppConst.PAGE_COURSEWORKS;
                    boolean isGroup = false;
                    if(indexGroup!= null) {
                        link += "/group" + indexGroup;
                        isGroup = true;
                    }
                    if(indexDiscipline !=null){
                        if(isGroup)
                            link+="-discipline"+indexDiscipline;
                        else
                            link+="/discipline"+indexDiscipline;
                    }
                    bc.add(new ItemInfo(link,AppConst.ICON_COURSEWORK,AppConst.TITLE_COURSEWORK+courseworkName));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_THEMES,AppConst.TITLE_THEMES));
                break;
            case AppConst.PAGE_GROUPS:
                if(indexDiscipline!= null) {
                    String link = AppConst.PAGE_DISCIPLINES;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES+disciplineName));
                }
                if(indexCoursework!= null) {
                    String link = AppConst.PAGE_COURSEWORKS;
                    bc.add(new ItemInfo(link,AppConst.ICON_COURSEWORK,AppConst.TITLE_COURSEWORK+courseworkName));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS+groupName));
                break;
        }

        }catch (Exception e){
            e.printStackTrace();
            Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
        }
        if(bc.size() > 0)
            bc.get(bc.size()-1).setIsCurrent(true);

        //Показывать крошки если их более одной
        if(bc.size() > 1)
            addBreadCrumbs(bc);
        else
            addBreadCrumbs(null);
    }
    static public class ItemInfo{
        boolean isCurrent;
        String link;
        String icon;
        String text;

        public ItemInfo() {
        }

        public ItemInfo(String link, String icon, String text) {
            this.isCurrent = false;
            this.link = link;
            this.icon = icon;
            this.text = text;
        }

        public boolean getIsCurrent() {
            return isCurrent;
        }

        public void setIsCurrent(boolean current) {
            isCurrent = current;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


    public interface Model extends TemplateModel{
        void setBreadcrumbsList(List<ItemInfo> data);
    }
}
