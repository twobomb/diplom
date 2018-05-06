package com.twobomb.ui.components;


import com.twobomb.Utils.AppConst;
import com.twobomb.app.security.SecurityUtils;
import com.twobomb.entity.Group;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
@Tag("bread-crumbs")
@HtmlImport("components/BreadCrumbs.html")
public class BreadCrumbs extends PolymerTemplate<BreadCrumbs.Model>{
    public static enum ParseIndexes{GROUP,DISCIPLINE,COURSEWORK};

    public void addBreadCrumbs(List<ItemInfo> items){
        getModel().setBreadcrumbsList(items);
    }

    public static Integer parseIndex(String indexSegment,ParseIndexes p){
        //group1-discipline1-coursework1
        Pattern pattern =  Pattern.compile("(group(\\d+)-?)?(discipline(\\d+)-?)?(coursework(\\d+))?");
        Matcher matcher = pattern.matcher(indexSegment);
        Integer indexGroup = null ,indexDiscipline= null, indexCoursework= null;
        if(matcher.matches())
            switch (p){
                case GROUP:
                    pattern = Pattern.compile("group(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexGroup = Integer.parseInt(matcher.group(1));
                    if(indexGroup != null && !SecurityUtils.isAccessGranted(Group.class))
                        indexGroup = null;
                    return indexGroup;
                case COURSEWORK:
                    pattern = Pattern.compile("coursework(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexCoursework = Integer.parseInt(matcher.group(1));
                    return indexCoursework;
                case DISCIPLINE:
                    pattern = Pattern.compile("discipline(\\d+)");
                    matcher = pattern.matcher(indexSegment);
                    if(matcher.find())
                        indexDiscipline = Integer.parseInt(matcher.group(1));
                    return indexDiscipline;
            }
        return null;
    }
    public void init(Location location){

        String href = location.getFirstSegment().isEmpty() ? AppConst.PAGE_DEFAULT
                : location.getFirstSegment();
        String indexSegment;
        if(location.getSegments().size() >= 2)
            indexSegment = location.getSegments().get(1);
        else
            indexSegment = "";
        Integer indexGroup = parseIndex(indexSegment,ParseIndexes.GROUP);
        Integer indexDiscipline = parseIndex(indexSegment,ParseIndexes.DISCIPLINE);
        Integer indexCoursework = parseIndex(indexSegment,ParseIndexes.COURSEWORK);

        List<ItemInfo> bc= new ArrayList<>();
        switch (href){
            case AppConst.PAGE_COURSEWORKS:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    if(indexDiscipline!= null)
                        link+= "/discipline"+indexDiscipline;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS));
                }
                if(indexDiscipline != null){
                    String link = "/"+AppConst.PAGE_DISCIPLINES;
                    if(indexGroup!= null)
                        link+= "/group"+indexGroup;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_COURSEWORK,AppConst.TITLE_COURSEWORK));
                break;
            case AppConst.PAGE_DISCIPLINES:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES));
                break;
            case AppConst.PAGE_THEMES:
                if(indexGroup != null){
                    String link = "/"+AppConst.PAGE_GROUPS;
                    if(indexDiscipline!= null)
                        link+= "/discipline"+indexDiscipline;
                    bc.add(new ItemInfo(link,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS));
                }
                if(indexDiscipline != null){
                    String link = "/"+AppConst.PAGE_DISCIPLINES;
                    if(indexGroup!= null)
                        link+= "/group"+indexGroup;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES));
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
                    bc.add(new ItemInfo(link,AppConst.ICON_COURSEWORK,AppConst.TITLE_COURSEWORK));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_THEMES,AppConst.TITLE_THEMES));
                break;
            case AppConst.PAGE_GROUPS:
                if(indexDiscipline!= null) {
                    String link = AppConst.PAGE_DISCIPLINES;
                    bc.add(new ItemInfo(link,AppConst.ICON_DISCIPLINES,AppConst.TITLE_DISCIPLINES));
                }
                bc.add(new ItemInfo("/"+href,AppConst.ICON_GROUP,AppConst.TITLE_GROUPS));
                break;
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
