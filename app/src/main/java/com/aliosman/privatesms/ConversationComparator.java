/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms;

import com.aliosman.privatesms.Model.Conversation;
import java.util.Comparator;
import java.util.Date;

public class ConversationComparator  implements Comparator<Conversation> {
    @Override
    public int compare(Conversation item, Conversation t1) {
        return (new Date(t1.getDate()).compareTo(new Date(item.getDate())));
    }
}