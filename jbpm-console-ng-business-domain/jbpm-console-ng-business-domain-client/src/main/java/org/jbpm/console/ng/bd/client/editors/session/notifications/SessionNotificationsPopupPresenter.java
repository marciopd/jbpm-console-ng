/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.client.editors.session.notifications;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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

@Dependent
@WorkbenchPopup(identifier = "Session Notifications Popup")
public class SessionNotificationsPopupPresenter {

    public interface InboxView
            extends
            UberView<SessionNotificationsPopupPresenter> {

        void displayNotification( String text );

        TextBox getSessionIdText();

        TextArea getSessionNotificationsTextArea();

        Button getUpdateButton();
    }

    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    Identity  identity;

//    @Inject
//    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    @Inject
    private Event<BeforeClosePlaceEvent>             closePlaceEvent;
    private PlaceRequest                             place;

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Session Notifications Popup";
    }

    @WorkbenchPartView
    public UberView<SessionNotificationsPopupPresenter> getView() {
        return view;
    }

    public void getSessionNotifications( final int sessionId ) {

//        knowledgeServices.call( new RemoteCallback<List<RuleNotificationSummary>>() {
//            @Override
//            public void callback( List<RuleNotificationSummary> notifications ) {
//                String notificationsText = "";
//                for ( RuleNotificationSummary n : notifications ) {
//                    notificationsText += n.getDataTimeStamp().toString() + " - " + n.getNotification() + "\n";
//                }
//                view.getSessionNotificationsTextArea().setText( "" );
//                view.getSessionNotificationsTextArea().setText( notificationsText );
//                view.displayNotification( " Session Notifications updated" );
//
//            }
//        } ).getAllNotificationInstance();

    }

    @OnReveal
    public void onReveal() {
        int taskId = Integer.parseInt( place.getParameter( "sessionId", "0" ).toString() );
        view.getSessionIdText().setText( String.valueOf( taskId ) );
        getSessionNotifications( Integer.parseInt( view.getSessionIdText().getText() ) );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }
}
