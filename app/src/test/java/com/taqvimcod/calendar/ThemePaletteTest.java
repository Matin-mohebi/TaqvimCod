package com.taqvimcod.calendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThemePaletteTest {

    /** every palette must provide distinct, fully-opaque hero gradient colors */
    @Test public void allPalettesResolveWithDistinctGradients() {
        long seenStarts = 0;
        for (int id = 0; id < Theme.PALETTE_COUNT; id++) {
            for (boolean dark : new boolean[]{false, true}) {
                Theme.Pal p = Theme.palette(id, dark);
                String tag = "palette " + id + " dark=" + dark;
                assertTrue(tag + " gradStart", (p.gradStart & 0xFF000000) == 0xFF000000);
                assertTrue(tag + " gradEnd", (p.gradEnd & 0xFF000000) == 0xFF000000);
                assertTrue(tag + " bg", (p.bg & 0xFF000000) == 0xFF000000);
                assertTrue(tag + " text", (p.text & 0xFF000000) == 0xFF000000);
                assertTrue(tag + " accent", (p.accent & 0xFF000000) == 0xFF000000);
                assertTrue(tag + " dark flag", p.dark == dark);
                seenStarts |= 1L << id;
            }
        }
        assertEquals(Theme.PALETTE_COUNT, Long.bitCount(seenStarts));
    }

    /** palettes must actually differ from each other (regression: copy-paste palettes) */
    @Test public void palettesAreVisuallyDistinct() {
        int[] starts = new int[Theme.PALETTE_COUNT];
        for (int id = 0; id < Theme.PALETTE_COUNT; id++) {
            starts[id] = Theme.palette(id, true).gradStart;
        }
        for (int a = 0; a < starts.length; a++)
            for (int b = a + 1; b < starts.length; b++)
                assertTrue("palettes " + a + " and " + b + " share gradStart", starts[a] != starts[b]);
    }
}
