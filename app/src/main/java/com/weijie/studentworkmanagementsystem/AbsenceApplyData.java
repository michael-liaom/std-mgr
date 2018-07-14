package com.weijie.studentworkmanagementsystem;

import java.util.Date;

public class AbsenceApplyData {
    String applyTo;
    String type;
    String cause;
    Date begin, ending;

    AbsenceApplyData() {
        begin = new Date();
        ending = new Date();
    }
}
