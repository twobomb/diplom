package com.twobomb.ui.pages;

import com.helger.commons.io.stream.StringInputStream;
import com.twobomb.Utils.AppConst;
import com.twobomb.entity.Role;
import com.twobomb.entity.User;
import com.twobomb.service.DisciplineService;
import com.twobomb.template_engine.WordController;
import com.twobomb.ui.MainView;
import com.twobomb.ui.models.MainModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.function.ContentTypeResolver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.*;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinServletResponse;
import elemental.client.Browser;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StateNode;
import org.apache.catalina.connector.Response;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Tag("report-view")
@HtmlImport("templates/ReportView.html")
@Route(value = AppConst.PAGE_REPORT,layout = MainView.class)
@PageTitle(AppConst.TITLE_REPORT)

@Secured({Role.TEACHER, Role.ADMIN})
public class ReportView extends PolymerTemplate<ReportView.Model> {

    @Id("grid")
    Grid<ReportInfoGroups> grid;

    @Id("createReport")
    Button button;

    @Id("multiSelect")
    Checkbox isMultiSelect;

    @Id("isDefaultPattern")
    Checkbox isDefaultPattern;

    @Id("uploadPattern")
    Upload upload;

    MultiFileMemoryBuffer buffer=  new MultiFileMemoryBuffer();


    @Autowired
    public ReportView(User currentUser, DisciplineService disciplineService) {
    try {
        List<ReportInfoGroups> infoGroups = disciplineService.getListReportInfoGroups();
        upload.setReceiver(buffer);
upload.setAutoUpload(true);
        grid.setItems(infoGroups);

        grid.addColumn(ReportInfoGroups::getIndex).setHeader("#");
        grid.addColumn(ReportInfoGroups::getGroup).setHeader("Группа");
        grid.addColumn(ReportInfoGroups::getChecked).setHeader("Все темы выбраны");

        isMultiSelect.addValueChangeListener(valueChangeEvent -> {
            grid.setSelectionMode(valueChangeEvent.getValue()?Grid.SelectionMode.MULTI:Grid.SelectionMode.SINGLE);
            grid.setItems(infoGroups);
        });
        isMultiSelect.setValue(false);
        isDefaultPattern.addValueChangeListener(valueChangeEvent -> {
            upload.setVisible(!valueChangeEvent.getValue());
        });

        button.addClickListener(buttonClickEvent -> {

            if(grid.getSelectedItems().size() == 0) {
                Notification.show("Выберите хотя бы одну группу", 2000, Notification.Position.BOTTOM_START);
                return;
            }
            HashMap<String,Object> map = null;
            if(isMultiSelect.getValue()){
                List<Long> indexes = new ArrayList<>();
                for(ReportInfoGroups inf: grid.getSelectedItems())
                    indexes.add(Long.valueOf(inf.indexOfMainList));
                try {
                    map = disciplineService.getMultiHashMapByGroupIndex(indexes);
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
                }
            }
            else{
                try {
                    map = disciplineService.getSingleHashMapByGroupIndex(Long.valueOf(grid.getSelectedItems().iterator().next().indexOfMainList));
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
                }
            }



            InputStream fis = null;
            String filename =  "";
            if(isMultiSelect.getValue())
                filename = "pattern_all_groups.docx";
            else
                filename = "pattern_one_group.docx";
            if(isDefaultPattern.getValue())
                try {
                    fis = new FileInputStream(ServletContext.class.getClassLoader().getResource(filename).getFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            else{
                if(buffer.getFiles().size() == 0){
                    Notification.show("Выберите шаблон или используйте стандартный!", 2000, Notification.Position.BOTTOM_START);
                    return;
                }
                    if (buffer.getFiles().iterator().hasNext()) {
                        String file = buffer.getFiles().iterator().next();
//                        System.out.println(buffer.getFileData(file).getMimeType());
//                        try {
//
//                            System.out.println(buffer.getInputStream(file).read());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        fis = buffer.getInputStream(file);
                    }
            }
            try {

                final InputStream fis_final = fis;
                final HashMap<String,Object> map_final = map;
                VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
                    @Override
                    public boolean handleRequest(VaadinSession vaadinSession, VaadinRequest vaadinRequest, VaadinResponse vaadinResponse) throws IOException {
                        vaadinResponse.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        vaadinResponse.setHeader("Content-Disposition","attachment; filename=report.docx");
//                            vaadinResponse.getOutputStream().write("hello".getBytes());
                        WordController wc = new WordController();
                        try {
                            wc.convert(fis_final,vaadinResponse.getOutputStream(),map_final);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        vaadinSession.removeRequestHandler(this);
                        return true;
                    }
                });
                UI.getCurrent().getPage().reload();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    } catch (Exception e) {
        e.printStackTrace();
        Notification.show(e.getMessage(),5000, Notification.Position.BOTTOM_START);
    }
}



    public interface Model extends MainModel {
        void setGroupsInfo(List<ReportInfoGroups> d);
    }
    public static class ReportInfoGroups {
        String index;
        Integer indexOfMainList;
        String group;
        Boolean checked;

        public ReportInfoGroups() {
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public Integer getIndexOfMainList() {
            return indexOfMainList;
        }

        public void setIndexOfMainList(Integer indexOfMainList) {
            this.indexOfMainList = indexOfMainList;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getChecked() {
            return checked?"Да":"Нет";
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }
    }
}

