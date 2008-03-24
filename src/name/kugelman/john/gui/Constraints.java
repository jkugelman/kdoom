package name.kugelman.john.gui;

import java.awt.*;

/**
 * Convenience class that replaces {@link GridBagConstraints}. By chaining
 * together calls to the property methods all the constraints for a control can
 * be specified concisely on one line. For example:
 * <p>
 * <code><pre>
 * add(rulesPanel,                    new Constraints(0, 0).width(5).anchorWest  ().fillBoth().insets(5, 0, 5, 0));
 * add(passwordLabel,                 new Constraints(0, 1)         .anchorEast  ()           .insets(0, 3, 0, 0));
 * add(passwordTextField,             new Constraints(1, 1)         .anchorWest  ());
 * add(Box.createHorizontalStrut(20), new Constraints(2, 1)         .anchorWest  ());
 * add(retypeLabel,                   new Constraints(3, 1)         .anchorEast  ());
 * add(retypeTextField,               new Constraints(4, 1)         .anchorWest  ());
 * add(Box.createVerticalStrut(20),   new Constraints(0, 2).width(5).anchorCenter().fillBoth().insets(5, 0, 0, 0));
 * add(buttonPanel,                   new Constraints(0, 3).width(5).anchorEast  ());
 * </pre></code>
 * 
 * @see GridBagLayout
 * @see GridBagConstraints
 */
public class Constraints extends GridBagConstraints {
    public Constraints() {
    }
    
    public Constraints(int gridx, int gridy) {
        cell(gridx, gridy);
    }
    
    public Constraints cell(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
        
        return this;
    }
    
    public Constraints width(int gridwidth) {
        this.gridwidth = gridwidth;
        
        return this;
    }
    
    public Constraints height(int gridheight) {
        this.gridheight = gridheight;
        
        return this;
    }
    
    public Constraints size(int gridwidth, int gridheight) {
        this.gridwidth  = gridwidth;
        this.gridheight = gridheight;
        
        return this;
    }
    
    public Constraints weight(double weightx, double weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        
        return this;
    }
    
    public Constraints weightx(double weightx) {
        this.weightx = weightx;
        
        return this;
    }
    
    public Constraints weighty(double weighty) {
        this.weighty = weighty;
        
        return this;
    }
    
    public Constraints anchor(int anchor) {
        this.anchor = anchor;
        
        return this;
    }
    
    public Constraints anchorCenter        () { return anchor(CENTER);           }
    public Constraints anchorNorth         () { return anchor(NORTH);            }
    public Constraints anchorNortheast     () { return anchor(NORTHEAST);        }
    public Constraints anchorEast          () { return anchor(EAST);             }
    public Constraints anchorSoutheast     () { return anchor(SOUTHEAST);        }
    public Constraints anchorSouth         () { return anchor(SOUTH);            }
    public Constraints anchorSouthwest     () { return anchor(SOUTHWEST);        }
    public Constraints anchorWest          () { return anchor(WEST);             }
    public Constraints anchorNorthwest     () { return anchor(NORTHWEST);        }
    public Constraints anchorPageStart     () { return anchor(PAGE_START);       }
    public Constraints anchorPageEnd       () { return anchor(PAGE_END);         }
    public Constraints anchorLineStart     () { return anchor(LINE_START);       }
    public Constraints anchorLineEnd       () { return anchor(LINE_END);         }
    public Constraints anchorFirstLineStart() { return anchor(FIRST_LINE_START); }
    public Constraints anchorFirstLineEnd  () { return anchor(FIRST_LINE_END);   }
    public Constraints anchorLastLineStart () { return anchor(LAST_LINE_START);  }
    public Constraints anchorLastLineEnd   () { return anchor(LAST_LINE_END);    }

    public Constraints fill(int fill) {
        this.fill = fill;
        
        return this;
    }
    
    public Constraints fillNone      () { return fill(NONE);       }
    public Constraints fillHorizontal() { return fill(HORIZONTAL); }
    public Constraints fillVertical  () { return fill(VERTICAL);   }
    public Constraints fillBoth      () { return fill(BOTH);       }
    
    public Constraints insets(Insets insets) {
        this.insets = insets;
        
        return this;
    }
    
    public Constraints insets(int top, int left, int bottom, int right) {
        this.insets.top    = top;
        this.insets.left   = left;
        this.insets.bottom = bottom;
        this.insets.right  = right;
        
        return this;
    }
    
    public Constraints ipad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        
        return this;
    }
}
