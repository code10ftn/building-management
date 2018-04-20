package com.code10.kts.data;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.User;

import java.util.Date;

/**
 * Malfunction testing constants and utility methods.
 */
public class MalfunctionData {

    public static Malfunction getMalfunction(User user, Building building) {
        final Malfunction malfunction = new Malfunction();
        malfunction.setCreator(user);
        malfunction.setAssignee(building.getSupervisor());
        malfunction.setDescription("test");
        malfunction.setReportDate(new Date());
        malfunction.setWorkArea(WorkArea.DOORS);
        malfunction.setBuilding(building);

        return malfunction;
    }
}
