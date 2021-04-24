/*
 * Copyright 2010-2021 James Pether Sörling
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
package com.hack23.cia.service.external.esv.impl;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hack23.cia.testfoundation.AbstractFunctionalIntegrationTest;

/**
 * The Class AbstractEsvFunctionalIntegrationTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/cia-service-external-esv.xml",
		"classpath:META-INF/cia-service-external-common.xml", "classpath:/META-INF/cia-test-context.xml" })
public abstract class AbstractEsvFunctionalIntegrationTest extends AbstractFunctionalIntegrationTest {

	/**
	 * Instantiates a new abstract esv functional integration test.
	 */
	public AbstractEsvFunctionalIntegrationTest() {
		super();
	}

}
