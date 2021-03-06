/*
 *
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2008-2010], Hyperic, Inc.
 * This file is part of HQ.
 *
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 */

package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.ControlApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.ControlHistory;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ControlHistoryResponse;
import org.hyperic.hq.hqapi1.types.User;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.util.List;

public class ControlHistory_test extends ControlTestBase {

    public ControlHistory_test(String name) {
        super(name);
    }

    public void testControlHistoryInvalidResource() throws Exception {
        ControlApi api = getApi().getControlApi();

        Resource r = new Resource();
        r.setId(Integer.MAX_VALUE);

        ControlHistoryResponse response = api.getHistory(r);
        hqAssertFailureObjectNotFound(response);
    }

    public void testControlHistoryNoControlPlugin() throws Exception {
        ControlApi api = getApi().getControlApi();

        Resource localPlatform = getLocalPlatformResource(false, false);

        ControlHistoryResponse response = api.getHistory(localPlatform);
        hqAssertSuccess(response);

        assertEquals("Wrong number of items in control history", 0,
                     response.getControlHistory().size());
    }

    /**
     * To validate HQ-2080
     * 
     * @throws Exception
     */
    public void testControlHistoryValidResource() throws Exception {
        HQApi api = getApi();
        ControlApi cApi = getApi().getControlApi();

        Resource controllableResource = createControllableResource(api);

        StatusResponse executeResponse = cApi.executeAction(controllableResource,
                                                            "run", new String[0]);
        hqAssertSuccess(executeResponse);

        ControlHistory controlHistory = findControlHistory(controllableResource, 5);            

        // HQ-2080 eliminated Quartz when running a control action on 
        // a resource (non-group) for immediate execution, so there
        // should be no delays.
        long timeToStart = controlHistory.getStartTime() - controlHistory.getDateScheduled();
        assertTrue(timeToStart < SECOND);
                
        cleanupResource(api, controllableResource);
    }

    public void testControlHistoryNoPermission() throws Exception {
        HQApi api = getApi();

        Resource controllableResource = createControllableResource(api);

        List<User> users = createTestUsers(1);
        User user = users.get(0);

        HQApi apiUnpriv = getApi(user.getName(), TESTUSER_PASSWORD);
        ControlApi cApiUnpriv = apiUnpriv.getControlApi();

        ControlHistoryResponse response = cApiUnpriv.getHistory(controllableResource);
        hqAssertFailurePermissionDenied(response);

        deleteTestUsers(users);
        cleanupResource(api, controllableResource);
    }
}
