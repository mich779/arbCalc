package com.romanobori;

import com.romanobori.utils.CommonFunctions;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CommonFunctionsTest {

    @Test
    public void round() {
        assertThat(102.24, is(CommonFunctions.round(102.248657, 2)));

        assertThat(CommonFunctions.round(102.0, 2), is(102.00));
    }
}