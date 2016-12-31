/**
 * Created by Richard on 12/30/2016.
 */
package com.cpen321.floatproject;

import android.content.Context;
import android.widget.EditText;

import com.cpen321.floatproject.utilities.Algorithms;
import com.cpen321.floatproject.utilities.UtilityMethod;
import com.firebase.client.DataSnapshot;

import junit.framework.Assert;

import org.junit.Test;


public class UtilityMethodTest {

    @Test
    public void testTextToDouble() {
        EditText s = null;
        assert UtilityMethod.text_to_double(s) == 0 ? true : false;
    }

}
