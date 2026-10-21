/* SPDX-License-Identifier: LGPL-3.0-only
 * Copyright (c) 2026 17Artist
 */
package org.embeddedt.embeddium.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionFlags;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;

/** Regression oracle: the old geometry walk filtered by translucent flags, including reset/order. */
public final class TerrainIndexCheck {
    private static final class Section extends RenderSection {
        private final int index, flags;
        Section(int index, int flags) { super(null, 0, 0, 0); this.index=index; this.flags=flags; }
        @Override public int getSectionIndex() { return index; }
        @Override public int getFlags() { return flags; }
    }
    public static void main(String[] args) {
        Random random = new Random(0x17312026);
        ChunkRenderList list = new ChunkRenderList(null);
        long opaqueVisits=0, indexedVisits=0;
        for (int frame=0; frame<1000; frame++) {
            list.reset(frame);
            if (list.sectionsWithTranslucencyIterator()!=null) throw new AssertionError("stale reset");
            int size=frame<3 ? new int[]{0,1,RenderRegion.REGION_SIZE}[frame] : random.nextInt(RenderRegion.REGION_SIZE+1);
            List<Integer> order=new ArrayList<>();
            for(int i=0;i<RenderRegion.REGION_SIZE;i++)order.add(i);
            Collections.shuffle(order,random);
            int[] flags=new int[RenderRegion.REGION_SIZE];
            for(int i=0;i<size;i++) {
                int index=order.get(i);
                flags[index]=random.nextInt(32);
                list.add(new Section(index,flags[index]));
            }
            List<Integer> expected=new ArrayList<>();
            ByteIterator old=list.sectionsWithGeometryIterator(false);
            while(old!=null&&old.hasNext()) {
                int index=old.nextByteAsInt();opaqueVisits++;
                if((flags[index]&(1<<RenderSectionFlags.HAS_TRANSLUCENT_DATA))!=0)expected.add(index);
            }
            List<Integer> actual=new ArrayList<>();
            ByteIterator next=list.sectionsWithTranslucencyIterator();
            while(next!=null&&next.hasNext())actual.add(next.nextByteAsInt());
            indexedVisits+=actual.size();
            if(!expected.equals(actual)||actual.size()!=list.getSectionsWithTranslucencyCount())
                throw new AssertionError("Selection or order changed in frame "+frame);
            if(size==RenderRegion.REGION_SIZE) {
                try { list.add(new Section(0,0)); throw new AssertionError("Overflow accepted"); }
                catch(ArrayIndexOutOfBoundsException expectedOverflow) { }
            }
        }
        System.out.println("1000 region rebuilds passed; oracle visits="+opaqueVisits+", indexed visits="+indexedVisits);
    }
}
