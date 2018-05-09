package com.twobomb.ui.datacontainers;

import com.twobomb.Utils.AppConst;
import com.twobomb.ui.components.BreadCrumbs;

public class IndexData {

    private String indexCoursework;
    private String indexGroup;
    private String indexDiscipline;
    private String courseworkPage;
    private String themePage;
    private String groupPage;
    private String disciplinePage;

    public IndexData() {
        initPages();
    }
    private void initPages(){
        this.courseworkPage = AppConst.PAGE_COURSEWORKS;
        this.disciplinePage = AppConst.PAGE_DISCIPLINES;
        this.themePage = AppConst.PAGE_THEMES;
        this.groupPage = AppConst.PAGE_GROUPS;
    }

    public IndexData(String indexCoursework, String indexGroup, String indexDiscipline) {
        this.indexCoursework = indexCoursework;
        this.indexGroup = indexGroup;
        this.indexDiscipline = indexDiscipline;
        initPages();
    }
    public static IndexData Instance(String indexSegment){
        Long c = BreadCrumbs.parseIndex(indexSegment, BreadCrumbs.ParseIndexes.COURSEWORK);
        Long g = BreadCrumbs.parseIndex(indexSegment, BreadCrumbs.ParseIndexes.GROUP);
        Long d = BreadCrumbs.parseIndex(indexSegment, BreadCrumbs.ParseIndexes.DISCIPLINE);
        IndexData id = new IndexData(
                c == null?null:String.valueOf(c),
                g == null?null:String.valueOf(g),
                d == null?null:String.valueOf(d)
        );
        return id;

    }

    //region GET SET
    public String getCourseworkPage() {
        return courseworkPage;
    }

    public void setCourseworkPage(String courseworkPage) {
        this.courseworkPage = courseworkPage;
    }

    public String getThemePage() {
        return themePage;
    }

    public void setThemePage(String themePage) {
        this.themePage = themePage;
    }

    public String getGroupPage() {
        return groupPage;
    }

    public void setGroupPage(String groupPage) {
        this.groupPage = groupPage;
    }

    public String getDisciplinePage() {
        return disciplinePage;
    }

    public void setDisciplinePage(String disciplinePage) {
        this.disciplinePage = disciplinePage;
    }

    public String getIndexCoursework() {
        return indexCoursework;
    }

    public void setIndexCoursework(String indexCoursework) {
        this.indexCoursework = indexCoursework;
    }

    public String getIndexGroup() {
        return indexGroup;
    }

    public void setIndexGroup(String indexGroup) {
        this.indexGroup = indexGroup;
    }

    public String getIndexDiscipline() {
        return indexDiscipline;
    }

    public void setIndexDiscipline(String indexDiscipline) {
        this.indexDiscipline = indexDiscipline;
    }
    //endregion
}
