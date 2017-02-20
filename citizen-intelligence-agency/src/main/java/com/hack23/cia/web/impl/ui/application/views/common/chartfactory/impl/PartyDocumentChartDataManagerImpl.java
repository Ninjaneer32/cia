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
package com.hack23.cia.web.impl.ui.application.views.common.chartfactory.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.options.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack23.cia.model.internal.application.data.document.impl.RiksdagenDocumentPartySummaryEmbeddedId;
import com.hack23.cia.model.internal.application.data.document.impl.ViewRiksdagenPartyDocumentDailySummary;
import com.hack23.cia.service.api.ApplicationManager;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.views.common.chartfactory.api.ChartOptions;
import com.hack23.cia.web.impl.ui.application.views.common.chartfactory.api.PartyDocumentChartDataManager;
import com.vaadin.ui.AbstractOrderedLayout;

/**
 * The Class PartyDocumentChartDataManagerImpl.
 */
@Service
public final class PartyDocumentChartDataManagerImpl extends AbstractChartDataManagerImpl implements PartyDocumentChartDataManager {

	private static final String DOCUMENT_HISTORY_PARTY = "Document history party";

	/** The Constant NO_INFO. */
	private static final String NO_INFO = "NoInfo";

	/** The Constant LOG_MSG_MISSING_DATA_FOR_KEY. */
	private static final String LOG_MSG_MISSING_DATA_FOR_KEY = "missing data for key:{}";

	/** The Constant EMPTY_STRING. */
	private static final String EMPTY_STRING = "";

	/** The Constant UNDER_SCORE. */
	private static final String UNDER_SCORE = "_";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PartyDocumentChartDataManagerImpl.class);

	/** The Constant DD_MMM_YYYY. */
	private static final String DD_MMM_YYYY = "dd-MMM-yyyy";

	/** The application manager. */
	@Autowired
	private ApplicationManager applicationManager;

	/** The chart options. */
	@Autowired
	private ChartOptions chartOptions;


	/**
	 * Instantiates a new party document chart data manager impl.
	 */
	public PartyDocumentChartDataManagerImpl() {
		super();
	}


	/**
	 * Gets the view riksdagen party document daily summary map.
	 *
	 * @return the view riksdagen party document daily summary map
	 */
	private Map<String, List<ViewRiksdagenPartyDocumentDailySummary>> getViewRiksdagenPartyDocumentDailySummaryMap() {
		final DataContainer<ViewRiksdagenPartyDocumentDailySummary, RiksdagenDocumentPartySummaryEmbeddedId> politicianBallotSummaryDailyDataContainer = applicationManager
				.getDataContainer(ViewRiksdagenPartyDocumentDailySummary.class);

		return politicianBallotSummaryDailyDataContainer.getAll().parallelStream().filter(Objects::nonNull)
				.collect(Collectors.groupingBy(t -> t.getEmbeddedId().getPartyShortCode().toUpperCase(Locale.ENGLISH).replace(UNDER_SCORE, EMPTY_STRING).trim()));
	}


	@Override
	public void createDocumentHistoryPartyChart(final AbstractOrderedLayout content,final String org) {
		final DataSeries dataSeries = new DataSeries();
		final Series series = new Series();

		final Map<String, List<ViewRiksdagenPartyDocumentDailySummary>> allMap = getViewRiksdagenPartyDocumentDailySummaryMap();

		final List<ViewRiksdagenPartyDocumentDailySummary> itemList = allMap
				.get(org.toUpperCase(Locale.ENGLISH).replace(UNDER_SCORE, EMPTY_STRING).trim());

		if (itemList != null) {

			final Map<String, List<ViewRiksdagenPartyDocumentDailySummary>> map = itemList.parallelStream()
					.filter(Objects::nonNull).collect(Collectors.groupingBy(
							t -> StringUtils.defaultIfBlank(t.getEmbeddedId().getDocumentType(), NO_INFO)));

			addDocumentHistoryByPartyData(dataSeries, series, map);
		}

		addChart(content, DOCUMENT_HISTORY_PARTY,new DCharts().setDataSeries(dataSeries).setOptions(chartOptions.createOptionsXYDateFloatLegendOutside(series)).show());
	}


	/**
	 * Adds the document history by party data.
	 *
	 * @param dataSeries
	 *            the data series
	 * @param series
	 *            the series
	 * @param map
	 *            the map
	 */
	private static void addDocumentHistoryByPartyData(final DataSeries dataSeries, final Series series,
			final Map<String, List<ViewRiksdagenPartyDocumentDailySummary>> map) {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DD_MMM_YYYY, Locale.ENGLISH);

		for (final Entry<String, List<ViewRiksdagenPartyDocumentDailySummary>> entry : map.entrySet()) {

			series.addSeries(new XYseries().setLabel(entry.getKey()));

			dataSeries.newSeries();
			if (entry.getValue() != null) {
				for (final ViewRiksdagenPartyDocumentDailySummary item : entry.getValue()) {
					if (item != null) {
						dataSeries.add(simpleDateFormat.format(item.getEmbeddedId().getPublicDate()), item.getTotal());
					}
				}
			} else {
				LOGGER.info(LOG_MSG_MISSING_DATA_FOR_KEY, entry);
			}

		}
	}

}
