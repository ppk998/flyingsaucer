package org.xhtmlrenderer.layout.block;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.layout.BlockFormattingContext;
import org.xhtmlrenderer.layout.Context;
import org.xhtmlrenderer.layout.content.ContentUtil;
import org.xhtmlrenderer.render.Box;

public class FloatUtil {

    public static void preChildrenLayout(Context c, Box block) {
        //CascadedStyle style = block.content.getStyle();
        //boolean set_bfc_float = false;
        //if (LayoutUtil.isFloated(style)) {
        BlockFormattingContext bfc = new BlockFormattingContext(block);
        //set_bfc_float = true;
        bfc.setWidth(block.width);
        c.pushBFC(bfc);
        //}
    }

    public static void postChildrenLayout(Context c, Box block) {
        //CascadedStyle style = block.content.getStyle();
        //if (LayoutUtil.isFloated(style)) {
        c.getBlockFormattingContext().doFinalAdjustments();
        c.popBFC();
        //}
    }

    public static void setupFloat(Context c, Box box, CascadedStyle style) {
        //CascadedStyle style = c.getCss().getCascadedStyle(box.element);
        if (ContentUtil.isFloated(style)) {
            // Uu.p("==== setup float ====");
            IdentValue floatVal = style.getIdent(CSSName.FLOAT);
            if ( floatVal == null || floatVal == IdentValue.NONE ) {
                return;
            }
            box.floated = true;
            if ( floatVal == IdentValue.LEFT ) {
                positionBoxLeft(c, box);
                c.getBlockFormattingContext().pushDownLeft(box);
                // Uu.p("final box = " + box);
                c.getBlockFormattingContext().addLeftFloat(box);
            }
            if ( floatVal == IdentValue.RIGHT ) {
                positionBoxRight(c, box);
                c.getBlockFormattingContext().pushDownRight(box);
                // Uu.p("final box = " + box);
                c.getBlockFormattingContext().addRightFloat(box);
            }
            // Uu.p("box = " + box);
            // Uu.p("==== end setup ====");
        }
    }

    private static void positionBoxLeft(Context c, Box box) {
        // Uu.p("positionBoxLeft()");
        // Uu.p("calling the new float routine");
        BlockFormattingContext bfc = c.getBlockFormattingContext();
        Box floater = bfc.getLeftFloatX(box);
        // Uu.p("floater = " + floater);
        // Uu.p("extents = " + c.getExtents());
        if (floater == null) {
            // Uu.p("no floater blocked. returning");
            box.x = 0;
            return;
        }


        box.x = floater.x + floater.width;

        if (box.x + box.width > c.getExtents().width &&
                box.width <= c.getExtents().width) {
            // Uu.p("not enough room!!!");
            // move the box to be below the last float and
            // try it again
            box.y = floater.y + floater.height;
            // Uu.p("trying again with box: " + box);
            positionBoxLeft(c, box);
            // Uu.p("final box = " + box);
        }
    }

    private static void positionBoxRight(Context c, Box box) {
        BlockFormattingContext bfc = c.getBlockFormattingContext();
        Box floater = bfc.getRightFloatX(box);
        if (floater == null) {
            // Uu.p("floaters are null");
            // Uu.p("extents = " + c.getExtents().width);
            box.x = c.getExtents().width - box.width;
            return;
        }

        box.x = floater.x - box.width;

        if (box.x < 0 &&
                box.width <= c.getExtents().width) {
            // Uu.p("not enough room!!!");
            // move the box to be below the last float and
            // try it again
            box.y = floater.y + floater.height;
            positionBoxRight(c, box);
        }
        // Uu.p("final box = " + box);
    }

}
