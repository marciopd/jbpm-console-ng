package org.jbpm.console.ng.ht.client.editors.taskcomments;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jbpm.console.ng.ht.client.i8n.Constants;

@Dependent
@WorkbenchPopup(identifier = "Task Comments Popup")
public class TaskCommentsPopupPresenter {

    public interface TaskCommentsPopupView extends UberView<TaskCommentsPopupPresenter> {
        Label getTaskIdText();

        Label getTaskNameText();

        UnorderedList getNavBarUL();

        TextArea getNewTaskCommentTextArea();

        Button addCommentButton();

        DataGrid<CommentSummary> getDataGrid();
        
        SimplePager getPager();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskCommentsPopupView view;

    @Inject
    Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    
    private Constants constants = GWT.create(Constants.class);

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private ListDataProvider<CommentSummary> dataProvider = new ListDataProvider<CommentSummary>();

    public ListDataProvider<CommentSummary> getDataProvider() {
        return dataProvider;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Comments();
    }

    @WorkbenchPartView
    public UberView<TaskCommentsPopupPresenter> getView() {
        return view;
    }
    
    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @OnReveal
    public void onReveal() {
        final long taskId = Long.parseLong(place.getParameter("taskId", "0").toString());
        view.getTaskIdText().setText(String.valueOf(taskId));
        view.getNavBarUL().clear();
        NavLink commentsLink = new NavLink(constants.Comments());
        commentsLink.setStyleName("active");

        NavLink workLink = new NavLink(constants.Work());

        workLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
                placeRequestImpl.addParameter("taskId", String.valueOf(taskId));
                placeManager.goTo(placeRequestImpl);
            }
        });
        NavLink detailsLink = new NavLink(constants.Details());
        detailsLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Task Details Popup");
                placeRequestImpl.addParameter("taskId", String.valueOf(taskId));
                placeManager.goTo(placeRequestImpl);
            }
        });

        view.getNavBarUL().add(workLink);
        view.getNavBarUL().add(detailsLink);
        view.getNavBarUL().add(commentsLink);
        refreshComments(taskId);
        view.getDataGrid().redraw();
    }

    public void refreshComments(long taskId) {
        taskServices.call(new RemoteCallback<TaskSummary>() {
            
            @Override
            public void callback(TaskSummary details) {
                view.getTaskIdText().setText(String.valueOf(details.getId()));
                view.getTaskNameText().setText(details.getName());
            }
        }).getTaskDetails(taskId);
        taskServices.call(new RemoteCallback<List<CommentSummary>>() {
            
            @Override
            public void callback(List<CommentSummary> comments) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll(comments);
                if (comments.size() > 0) {
                    view.getDataGrid().setHeight("350px");
                    view.getPager().setVisible(true);
                }
                dataProvider.refresh();
                view.getDataGrid().redraw();
            }
        }).getAllCommentsByTaskId(taskId);

    }

    public void addTaskComment(final long taskId, String text, Date addedOn) {
        taskServices.call(new RemoteCallback<Long>() {
            
            @Override
            public void callback(Long response) {
                refreshComments(taskId);
            }
        }).addComment(taskId, text, identity.getName(), addedOn);
    }
    
    public void addDataDisplay(HasData<CommentSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
