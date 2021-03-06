/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.util;

import static org.jbpm.console.ng.ht.client.util.DateUtils.createDate;

import java.util.Date;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestDateUtils extends GWTTestCase {

    @Test
    @SuppressWarnings("deprecation")
    public void testCreateDateWithDefaultFormat() {
        Date date = createDate("2013-05-01");
        System.out.println(date);
        assertEquals(2013, date.getYear() + 1900);
        assertEquals(05, date.getMonth() + 1);
        assertEquals(01, date.getDate());
    }

    @Test
    public void testCreateMalformedDateWithDefaultFormat() {
        try {
            // malformed date string
            createDate("2013-kk-05");
            fail("IllegalArgumentException expected for malformed input!");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testGetWeekRange() {
        Date date = createDate("2013-05-01");
        DateRange weekRange = DateUtils.getWeekDateRange(date);
        assertEquals(createDate("2013-04-29"), weekRange.getStartDate());
        assertEquals(createDate("2013-05-03"), weekRange.getEndDate());
        
        // part of the week is in 2013 and part in 2012
        date = createDate("2013-01-02");
        weekRange = DateUtils.getWeekDateRange(date);
        assertEquals(createDate("2012-12-31"), weekRange.getStartDate());
        assertEquals(createDate("2013-01-04"), weekRange.getEndDate());
        // same as above, but the specified date is in 2012
        date = createDate("2012-12-31");
        weekRange = DateUtils.getWeekDateRange(date);
        assertEquals(createDate("2012-12-31"), weekRange.getStartDate());
        assertEquals(createDate("2013-01-04"), weekRange.getEndDate());
    }

    @Test
    public void testGetMonthRange() {
        Date date = createDate("2013-04-25");
        DateRange monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-04-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-04-30"), monthRange.getEndDate());

        // December as last month
        date = createDate("2013-12-31");
        monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-12-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-12-31"), monthRange.getEndDate());

        // January as first month
        date = createDate("2013-01-01");
        monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-01-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-01-31"), monthRange.getEndDate());
    }

    @Override
    public String getModuleName() {
        return "org.jbpm.console.ng.ht.JbpmConsoleNGHumanTasksClient";
    }
}
