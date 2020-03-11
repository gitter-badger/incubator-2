/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2018 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imagej.ops2.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import net.imagej.mesh.Mesh;
import net.imagej.mesh.Triangle;
import net.imagej.ops2.features.AbstractFeatureTest;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.ops.core.builder.OpBuilder;

public class MeshFeatureTests extends AbstractFeatureTest {
	private static final double EPSILON = 10e-12;
	private static LabelRegion<String> ROI;
	private static Mesh mesh;

	@BeforeClass
	public static void setupBefore() {
		ROI = createLabelRegion(getTestImage3D(), 1, 255);
		mesh = getMesh();
	}

	@Test
	public void boxivityMesh() {
		try {
			op("geom.boxivity").input(mesh).apply();
		} catch (IllegalArgumentException e) {
			// DefaultSmallestOrientedBoundingBox is not implemented.
		}
	}

	@Test
	public void compactness() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.compactness", 0.572416357359835,
				op("geom.compactness").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void convexHull3D() {
		/**
		 * convexHull3D is tested in {@link QuickHull3DTest}.
		 */
	}

	@Test
	public void convexityMesh() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.convexity", 0.983930494866521,
				op("geom.convexity").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void mainElongation() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.mainElongation", 0.2079585956045953,
				(op("geom.mainElongation").input(mesh).outType(DoubleType.class).apply()).get(),
				EPSILON);
	}

	@Test
	public void marchingCubes() {
		final Mesh result = (Mesh) op("geom.marchingCubes").input(ROI, null, null).apply();
		assertEquals(mesh.triangles().size(), result.triangles().size());
		final Iterator<Triangle> expectedFacets = mesh.triangles().iterator();
		final Iterator<Triangle> actualFacets = result.triangles().iterator();
		while (expectedFacets.hasNext() && actualFacets.hasNext()) {
			final Triangle expected = expectedFacets.next();
			final Triangle actual = actualFacets.next();
			assertEquals(expected.v0x(), actual.v0x(), EPSILON);
			assertEquals(expected.v0y(), actual.v0y(), EPSILON);
			assertEquals(expected.v0z(), actual.v0z(), EPSILON);
			assertEquals(expected.v1x(), actual.v1x(), EPSILON);
			assertEquals(expected.v1y(), actual.v1y(), EPSILON);
			assertEquals(expected.v1z(), actual.v1z(), EPSILON);
			assertEquals(expected.v2x(), actual.v2x(), EPSILON);
			assertEquals(expected.v2y(), actual.v2y(), EPSILON);
			assertEquals(expected.v2z(), actual.v2z(), EPSILON);
		}
		assertTrue(!expectedFacets.hasNext() && !actualFacets.hasNext());
	}

	@Test
	public void medianElongation() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.medianElongation", 0.30059118825775455,
				op("geom.medianElongation").input(mesh).outType(DoubleType.class).apply().get(),
				EPSILON);
	}

	@Test
	public void sizeConvexHullMesh() {
		// ground truth computed with matlab
		assertEquals("geom.sizeConvexHull", 304.5,
				op("geom.sizeConvexHull").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void sizeMesh() {
		// ground truth computed with matlab
		assertEquals("geom.size", 257.5,
				op("geom.size").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void solidityMesh() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.solidity", 0.845648604269294,
				op("geom.solidity").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void spareness() {
		// formula verified
		assertEquals("geom.spareness", 0.7884710437076516,
				op("geom.spareness").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void sphericity() {
		// formula verified and ground truth computed with matlab
		assertEquals("geom.sphericity", 0.830304411183464,
				op("geom.sphericity").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void surfaceArea() {
		// ground truth computed with matlab
		assertEquals("geom.boundarySize", 235.7390893402464,
				op("geom.boundarySize").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);
	}

	@Test
	public void surfaceAreaConvexHull() {
		// ground truth computed with matlab
		assertEquals("geom.boundarySizeConvexHull", 231.9508788339317,
				op("geom.boundarySizeConvexHull").input(mesh).outType(DoubleType.class).apply().get(),
				EPSILON);
	}

	@Test
	public void verticesCountConvexHullMesh() {
		// verified with matlab
		assertEquals("geom.verticesCountConvexHull", 57,
				op("geom.verticesCountConvexHull").input(mesh).outType(DoubleType.class).apply().get(),
				EPSILON);
	}

	@Test
	public void verticesCountMesh() {
		// verified with matlab
		assertEquals("geom.verticesCount", 184,
				op("geom.verticesCount").input(mesh).outType(DoubleType.class).apply().get(), EPSILON);

	}

	@Test
	public void voxelization3D() {
		// https://github.com/imagej/imagej-ops/issues/422
	}
}
