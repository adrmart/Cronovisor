package com.uva.adrmart.cronovisor_v1.persistence;

import java.util.HashMap;

/**
 * Created by Adrian on 25/06/2016.
 */
public interface NotificationDao {

    HashMap<Integer, Integer> getNotifications();

    void addNotification(int id);
}
