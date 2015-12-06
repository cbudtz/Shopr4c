package com.nexb.shopr4;

import com.nexb.shopr4.dataModel.User;

/**
 * Created by Christian on 04-12-2015.
 */
public interface IUserDataListener {

    void userdataChanged(User user);
}
