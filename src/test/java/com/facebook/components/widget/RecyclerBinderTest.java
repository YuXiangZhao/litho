// Copyright 2004-present Facebook. All Rights Reserved.

package com.facebook.litho.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentInfo;
import com.facebook.litho.ComponentTree;
import com.facebook.litho.LayoutHandler;
import com.facebook.litho.Size;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RecyclerBinder}
 */
@RunWith(ComponentsTestRunner.class)
@PrepareForTest(ComponentTreeHolder.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public class RecyclerBinderTest {

  @Rule
  public PowerMockRule mPowerMockRule = new PowerMockRule();

  private static final float RANGE_RATIO = 2.0f;
  private static final int RANGE_SIZE = 3;
  private final Map<Component, TestComponentTreeHolder> mHoldersForComponents = new HashMap<>();
  private RecyclerBinder mRecyclerBinder;
  private LayoutInfo mLayoutInfo;
  private ComponentContext mComponentContext;

  private final Answer<ComponentTreeHolder> mComponentTreeHolderAnswer =
      new Answer<ComponentTreeHolder>() {
        @Override
        public ComponentTreeHolder answer(InvocationOnMock invocation) throws Throwable {
          final ComponentInfo componentInfo = (ComponentInfo) invocation.getArguments()[0];
          final TestComponentTreeHolder holder = new TestComponentTreeHolder(componentInfo);
          mHoldersForComponents.put(componentInfo.getComponent(), holder);

          return holder;
        }
      };

  @Before
  public void setup() {
    mComponentContext = new ComponentContext(RuntimeEnvironment.application);
    PowerMockito.mockStatic(ComponentTreeHolder.class);
    PowerMockito.when(ComponentTreeHolder.acquire(
        any(ComponentInfo.class),
        any(LayoutHandler.class)))
