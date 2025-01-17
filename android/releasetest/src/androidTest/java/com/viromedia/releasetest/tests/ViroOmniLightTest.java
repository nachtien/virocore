//
//  Copyright (c) 2017-present, ViroMedia, Inc.
//  All rights reserved.
//
//  Permission is hereby granted, free of charge, to any person obtaining
//  a copy of this software and associated documentation files (the
//  "Software"), to deal in the Software without restriction, including
//  without limitation the rights to use, copy, modify, merge, publish,
//  distribute, sublicense, and/or sell copies of the Software, and to
//  permit persons to whom the Software is furnished to do so, subject to
//  the following conditions:
//
//  The above copyright notice and this permission notice shall be included
//  in all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
//  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
//  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
//  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
//  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.viromedia.releasetest.tests;

import android.graphics.Color;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.collect.Iterables;
import com.viro.core.DirectionalLight;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.OmniLight;
import com.viro.core.Sphere;
import com.viro.core.Surface;
import com.viro.core.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by manish on 10/25/17.
 */

@RunWith(AndroidJUnit4.class)
public class ViroOmniLightTest extends ViroBaseTest {
    private static final String TAG = ViroAmbientLightTest.class.getName();
    private static final int INFLUENCE_BITMASK = 1;
    private OmniLight mOmniLight;
    private Node mVSurface;
    private Node mHSurface;
    private Node mSphere;

    @Override
    void configureTestScene() {
        mOmniLight = new OmniLight();
        mOmniLight.setInfluenceBitMask(INFLUENCE_BITMASK);
        mOmniLight.setIntensity(1000f);
        mOmniLight.setPosition(new Vector(0, 5, -2));
        mOmniLight.setAttenuationStartDistance(2);
        mOmniLight.setAttenuationEndDistance(Float.MAX_VALUE);
        mScene.getRootNode().addLight(mOmniLight);

        final DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(new Vector(-1, 0, 1));
        directionalLight.setInfluenceBitMask(INFLUENCE_BITMASK);
        mScene.getRootNode().addLight(directionalLight);
        // Configure vertical surface
        mVSurface = new Node();
        final Surface surface = new Surface(5, 5);
        final float[] surfacePos = {0, 0, -5};
        mVSurface.setPosition(new Vector(surfacePos));
        final Material surfaceMaterial = new Material();
        surfaceMaterial.setDiffuseColor(Color.WHITE);
        surfaceMaterial.setLightingModel(Material.LightingModel.BLINN);
        surface.setMaterials(Arrays.asList(surfaceMaterial));
        mVSurface.setGeometry(surface);
        mVSurface.setLightReceivingBitMask(INFLUENCE_BITMASK);
        mScene.getRootNode().addChildNode(mVSurface);

        // Configure horizontal surface
        mHSurface = new Node();
        final Surface hSurface = new Surface(5, 5);
        final float[] hSurfacePos = {0, -2f, -5};
        mHSurface.setPosition(new Vector(hSurfacePos));
        final Material hSurfaceMaterial = new Material();
        hSurfaceMaterial.setDiffuseColor(Color.WHITE);
        hSurfaceMaterial.setLightingModel(Material.LightingModel.BLINN);
        hSurface.setMaterials(Arrays.asList(hSurfaceMaterial));
        mHSurface.setGeometry(hSurface);
        mHSurface.setRotation(new Vector(-90, 0, 0));
        mHSurface.setLightReceivingBitMask(INFLUENCE_BITMASK);
        mScene.getRootNode().addChildNode(mHSurface);

        // Configure sphere
        mSphere = new Node();
        final Sphere sphere = new Sphere(0.5f);
        final Material sphereMaterial = new Material();
        sphereMaterial.setDiffuseColor(Color.WHITE);
        sphereMaterial.setLightingModel(Material.LightingModel.BLINN);
        mSphere.setShadowCastingBitMask(INFLUENCE_BITMASK);
        mSphere.setLightReceivingBitMask(INFLUENCE_BITMASK);
        mSphere.setGeometry(sphere);
        final float[] spherePos = {0, -0.5f, -2f};
        mSphere.setPosition(new Vector(spherePos));
        sphere.setMaterials(Arrays.asList(sphereMaterial));
        mScene.getRootNode().addChildNode(mSphere);
    }

    @Test
    public void testOmniLight() {
        runUITest(() -> testSetColor());
        runUITest(() -> testSetIntensity());
        runUITest(() -> testSetInfluenceBitMask());
        runUITest(() -> testSetPosition());
        runUITest(() -> testSetAttenuationEndDistance());
        runUITest(() -> testSetAttenuationStartDistance());
    }

    private void testSetColor() {
        final List<Integer> colors = Arrays.asList(Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN);

        final Iterator<Integer> itr = Iterables.cycle(colors).iterator();

        mMutableTestMethod = () -> {
            mOmniLight.setColor(itr.next());
        };
        assertPass("Cycling colors through WHITE, RED, GREEN, BLUE, MAGENTA, CYAN");
    }

    private void testSetIntensity() {
        mMutableTestMethod = () -> {
            final float currentIntensity = mOmniLight.getIntensity();
            mOmniLight.setIntensity((currentIntensity + 200) % 2000);
        };
        assertPass("Cycling intensity from 0 to 2000, with +200 every second");
    }

    private void testSetInfluenceBitMask() {
        mMutableTestMethod = () -> {
            final int currentBitMask = mOmniLight.getInfluenceBitMask();
            mOmniLight.setInfluenceBitMask(currentBitMask == INFLUENCE_BITMASK ? 2 : INFLUENCE_BITMASK);
        };

        assertPass("Alternating omnilight's influence betweeen 1 and 2");
    }

    private void testSetPosition() {
        final Vector initialPosition = mOmniLight.getPosition();
        mMutableTestMethod = () -> {
            final Vector currentPostion = mOmniLight.getPosition();
            mOmniLight.setPosition(new Vector(currentPostion.x, currentPostion.y + 10, currentPostion.z));
        };

        assertPass("Moving Omnilight in +Y direction, vertical plane should go dark");
    }

    private void testSetAttenuationEndDistance() {
        final List<Integer> endDistance = Arrays.asList(12, 11, 10, 9);
        final Iterator<Integer> itr = Iterables.cycle(endDistance).iterator();

        mMutableTestMethod = () -> {
            // TODO Fix this test to use getAttenuationEndDistance after VIRO-2097 is fixed
            mOmniLight.setAttenuationEndDistance(itr.next());
        };

        assertPass("Decreasing attenuationEndDistance");
    }

    private void testSetAttenuationStartDistance() {
        mOmniLight.setAttenuationEndDistance(12);
        final List<Integer> startDistance = Arrays.asList(9, 10, 11, 12);
        final Iterator<Integer> itr = Iterables.cycle(startDistance).iterator();

        mMutableTestMethod = () -> {
            // TODO Fix this test to use getAttenuationEndDistance after VIRO-2097 is fixed
            mOmniLight.setAttenuationStartDistance(itr.next());
        };

        assertPass("Increasing attenuationStartDistance");
    }
}
