package me.jellysquid.mods.sodium.client.render.chunk.compile;

import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.ints.IntArrays;

/** Stable far-to-near ordering for the private per-task quad index array. */
final class TranslucentDistanceSort {
    private static final int RADIX_THRESHOLD = 4096;

    static int[] sort(float[] distances) {
        int[] indices = new int[distances.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        if (indices.length < RADIX_THRESHOLD || hasNaN(distances)) {
            IntArrays.mergeSort(indices, (a, b) -> Float.compare(distances[b], distances[a]));
            return indices;
        }

        FloatArrays.radixSortIndirect(indices, distances, true);
        reverse(indices, 0, indices.length);

        // Reversing ascending order also reverses ties. Restore their original order
        // so coplanar glass keeps exactly the same blending order as the stable merge.
        int start = 0;
        while (start < indices.length) {
            int end = start + 1;
            while (end < indices.length
                    && Float.compare(distances[indices[start]], distances[indices[end]]) == 0) {
                end++;
            }
            reverse(indices, start, end);
            start = end;
        }
        return indices;
    }

    private static boolean hasNaN(float[] distances) {
        for (float distance : distances) {
            if (Float.isNaN(distance)) {
                return true;
            }
        }
        return false;
    }

    private static void reverse(int[] indices, int from, int to) {
        for (int left = from, right = to - 1; left < right; left++, right--) {
            int value = indices[left];
            indices[left] = indices[right];
            indices[right] = value;
        }
    }
}
