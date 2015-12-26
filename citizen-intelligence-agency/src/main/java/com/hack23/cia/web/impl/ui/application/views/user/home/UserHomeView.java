/*
 * Copyright 2014 James Pether Sörling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.web.impl.ui.application.views.user.home;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hack23.cia.model.internal.application.system.impl.ApplicationActionEvent;
import com.hack23.cia.model.internal.application.system.impl.ApplicationActionEvent_;
import com.hack23.cia.model.internal.application.system.impl.ApplicationEventGroup;
import com.hack23.cia.model.internal.application.user.impl.UserAccount;
import com.hack23.cia.service.api.ApplicationManager;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.views.common.chartfactory.ChartDataManager;
import com.hack23.cia.web.impl.ui.application.views.common.formfactory.FormFactory;
import com.hack23.cia.web.impl.ui.application.views.common.gridfactory.GridFactory;
import com.hack23.cia.web.impl.ui.application.views.common.menufactory.MenuItemFactory;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.AdminViews;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.PageMode;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.UserViews;
import com.hack23.cia.web.impl.ui.application.views.pageclicklistener.PageItemPropertyClickListener;
import com.hack23.cia.web.impl.ui.application.views.user.common.AbstractUserView;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import ru.xpoft.vaadin.VaadinView;

/**
 * The Class PartyView.
 */
@Service
@Scope("prototype")
@VaadinView(value = UserHomeView.NAME, cached = true)
public final class UserHomeView extends AbstractUserView {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant NAME. */
	public static final String NAME = UserViews.USERHOME_VIEW_NAME;

	/** The application manager. */
	@Autowired
	@Qualifier("ApplicationManager")
	private transient ApplicationManager applicationManager;

	/** The chart data manager. */
	@Autowired
	private transient ChartDataManager chartDataManager;

	/** The menu item factory. */
	@Autowired
	private transient MenuItemFactory menuItemFactory;

	/** The grid factory. */
	@Autowired
	private transient GridFactory gridFactory;

	/** The form factory. */
	@Autowired
	private transient FormFactory formFactory;

	/**
	 * Post construct.
	 */
	@PostConstruct
	public void postConstruct() {
		setSizeFull();
		createBasicLayoutWithPanelAndFooter(NAME);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {

		final String parameters = event.getParameters();

		if (parameters != null) {

			final String pageId = parameters.substring(parameters.lastIndexOf('/') + "/".length(), parameters.length());

			menuItemFactory.createUserHomeMenuBar(getBarmenu(), pageId);

			final VerticalLayout panelContent = new VerticalLayout();
			panelContent.setSizeFull();
			panelContent.setMargin(true);

			if (StringUtils.isEmpty(parameters) || parameters.equals(pageId)
					|| parameters.contains(PageMode.Overview.toString())) {

				panelContent.addComponent(new Label("Overview"));


				final DataContainer<UserAccount, Long> dataContainer = applicationManager.getDataContainer(UserAccount.class);


					UserAccount userAccount = dataContainer.load(getUserIdFromSecurityContext());

					formFactory.addTextFields(panelContent, new BeanItem<UserAccount>(userAccount), UserAccount.class,
							Arrays.asList(new String[] { "username","createdDate","email","country","numberOfVisits" }));



					final DataContainer<ApplicationActionEvent, Long> eventDataContainer = applicationManager.getDataContainer(ApplicationActionEvent.class);


					final BeanItemContainer<ApplicationActionEvent> politicianDocumentDataSource = new BeanItemContainer<ApplicationActionEvent>(ApplicationActionEvent.class,
							eventDataContainer.findOrderedListByProperty(ApplicationActionEvent_.userId,userAccount.getUserId(),ApplicationActionEvent_.createdDate));

					panelContent.addComponent(gridFactory.createBasicBeanItemGrid(politicianDocumentDataSource, "ApplicationActionEvent",
							new String[] { "hjid", "createdDate", "eventGroup", "applicationOperation","page","pageMode","elementId","actionName","userId","sessionId","errorMessage","applicationMessage", "modelObjectVersion" },
							new String[] { "modelObjectId" }, "hjid",
							new PageItemPropertyClickListener(AdminViews.ADMIN_APPLICATIONS_EVENTS_VIEW_NAME, "hjid"), null));

			}







			getPanel().setContent(panelContent);
			getPanel().setCaption("Userhome:");

			pageActionEventHelper.createPageEvent(ViewAction.VISIT_USER_HOME_VIEW, ApplicationEventGroup.USER, NAME,
					parameters, pageId);

		}

	}

	private static Long getUserIdFromSecurityContext() {

		Long result=null;

		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				Object principal = authentication.getPrincipal();

				if (principal instanceof UserAccount) {
					UserAccount userAccount = (UserAccount) principal;
					result = userAccount.getHjid();
				} else {
					System.out.println(result);
				}
			}
		}

		return result;
	}

}