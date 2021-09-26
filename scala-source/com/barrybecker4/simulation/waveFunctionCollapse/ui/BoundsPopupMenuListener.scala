// Ported https://itqna.net/questions/31256/how-make-list-combobox-items-bigger-combo-itself to scala
package com.barrybecker4.simulation.waveFunctionCollapse.ui

import javax.swing.{JComboBox, JScrollBar, JScrollPane, ScrollPaneConstants, SwingUtilities}
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import javax.swing.plaf.basic.BasicComboPopup
import java.awt.Adjustable


/**
  * This class will change the bounds of the JComboBox popup menu to support
  * different functionality. It will support the following features:
  *  - a horizontal scrollbar can be displayed when necessary
  *  - the popup can be wider than the combo box
  *  - the popup can be displayed above the combo box
  *
  * Class will only work for a JComboBox that uses a BasicComboPop.
  * @param scrollBarRequired display a horizontal scrollbar when the
  *                          preferred width of popup is greater than width of scrollPane.
  * @param popupWider        display the popup at its preferred with
  * @param maximumWidth      limit the popup width to the value specified
  *                          (minimum size will be the width of the combo box)
  * @param popupAbove        display the popup above the combo box
  */
class BoundsPopupMenuListener(
  var scrollBarRequired: Boolean = true,
  var popupWider: Boolean = false,
  var maximumWidth: Int = -1,
  var popupAbove: Boolean = false) extends PopupMenuListener {

  private var scrollPane: JScrollPane = _

  /** Allows the display of a horizontal scrollbar when required. */
  def this() = this(true, false, -1, false)

  /**
    * Convenience constructor that allows you to display the popup
    * wider and/or above the combo box.
    * @param popupWider when true, popup width is based on the popup
    *                   preferred width
    * @param popupAbove when true, popup is displayed above the combobox
    */
  def this(popupWider: Boolean, popupAbove: Boolean) = this(false, popupWider, -1, popupAbove)

  /**
    * Convenience constructor that allows you to display the popup
    * wider than the combo box and to specify the maximum width
    * @param maximumWidth the maximum width of the popup. The
    *                     popupAbove value is set to "true".
    */
  def this(maximumWidth: Int) = {
    this(scrollBarRequired = true, popupWider = true, maximumWidth, popupAbove = false)
  }

  /**
    * Alter the bounds of the popup just before it is made visible.
    */
  override def popupMenuWillBecomeVisible(e: PopupMenuEvent): Unit = {
    val comboBox = e.getSource.asInstanceOf[JComboBox[_]]
    if (comboBox.getItemCount == 0) return
    val child = comboBox.getAccessibleContext.getAccessibleChild(0)
    child match {
      case comboPopup: BasicComboPopup => SwingUtilities.invokeLater(new Runnable() {
        override def run(): Unit = {
          customizePopup(comboPopup)
        }
      })
      case _ =>
    }
  }

  protected def customizePopup(popup: BasicComboPopup): Unit = {
    scrollPane = getScrollPane(popup)
    if (popupWider) popupWider(popup)
    checkHorizontalScrollBar(popup)
    //  For some reason in JDK7 the popup will not display at its preferred
    //  width unless its location has been changed from its default
    //  (ie. for normal "pop down" shift the popup and reset)
    val comboBox = popup.getInvoker
    val location = comboBox.getLocationOnScreen
    if (popupAbove) {
      val height = popup.getPreferredSize.height
      popup.setLocation(location.x, location.y - height)
    }
    else {
      val height = comboBox.getPreferredSize.height
      popup.setLocation(location.x, location.y + height - 1)
      popup.setLocation(location.x, location.y + height)
    }
  }

  /**
   *  Adjust the width of the scrollpane used by the popup
   */
  protected def popupWider(popup: BasicComboPopup): Unit = {
    val list = popup.getList
    //  Determine the maximum width to use:
    //  a) determine the popup preferred width
    //  b) limit width to the maximum if specified
    //  c) ensure width is not less than the scroll pane width
    var popupWidth = list.getPreferredSize.width + 5
    // make sure horizontal scrollbar doesn't appear + getScrollBarWidth(popup, scrollPane)
    if (maximumWidth != -1) popupWidth = Math.min(popupWidth + 16, maximumWidth)
    val scrollPaneSize = scrollPane.getPreferredSize
    popupWidth = Math.max(popupWidth, scrollPaneSize.width)
    //  Adjust the width
    scrollPaneSize.width = popupWidth
    scrollPane.setPreferredSize(scrollPaneSize)
    scrollPane.setMaximumSize(scrollPaneSize)
  }

  /**
    * This method is called every time:
    *  - to make sure the viewport is returned to its default position
    *  - to remove the horizontal scrollbar when it is not wanted
    */
  private def checkHorizontalScrollBar(popup: BasicComboPopup): Unit = { //  Reset the viewport to the left
    val viewport = scrollPane.getViewport
    val p = viewport.getViewPosition
    p.x = 0
    viewport.setViewPosition(p)
    //  Remove the scrollbar so it is never painted
    if (!scrollBarRequired) {
      scrollPane.setHorizontalScrollBar(null)
      return
    }
    //  Make sure a horizontal scrollbar exists in the scrollpane
    var horizontal = scrollPane.getHorizontalScrollBar
    if (horizontal == null) {
      horizontal = new JScrollBar(Adjustable.HORIZONTAL)
      scrollPane.setHorizontalScrollBar(horizontal)
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    }
    //  Potentially increase height of scroll pane to display the scrollbar
    if (horizontalScrollBarWillBeVisible(popup, scrollPane)) {
      val scrollPaneSize = scrollPane.getPreferredSize
      scrollPaneSize.height += horizontal.getPreferredSize.height
      scrollPane.setPreferredSize(scrollPaneSize)
      scrollPane.setMaximumSize(scrollPaneSize)
      scrollPane.revalidate()
    }
  }

  /**
   *  Get the scroll pane used by the popup so its bounds can be adjusted
   */
  protected def getScrollPane(popup: BasicComboPopup): JScrollPane = {
    val list = popup.getList
    val c = SwingUtilities.getAncestorOfClass(classOf[JScrollPane], list)
    c.asInstanceOf[JScrollPane]
  }

  /**
   *  I can't find any property on the scrollBar to determine if it will be
   *  displayed or not so use brute force to determine this.
   */
  protected def getScrollBarWidth(popup: BasicComboPopup, scrollPane: JScrollPane): Int = {
    var scrollBarWidth = 0
    val comboBox = popup.getInvoker.asInstanceOf[JComboBox[_]]
    if (comboBox.getItemCount > comboBox.getMaximumRowCount) {
      val vertical = scrollPane.getVerticalScrollBar
      scrollBarWidth = vertical.getPreferredSize.width
    }
    scrollBarWidth
  }

  /**
   * I can't find any property on the scrollBar to determine if it will be
   * displayed or not so use brute force to determine this.
   */
  protected def horizontalScrollBarWillBeVisible(popup: BasicComboPopup, scrollPane: JScrollPane): Boolean = {
    val list = popup.getList
    val scrollBarWidth = getScrollBarWidth(popup, scrollPane)
    val popupWidth = list.getPreferredSize.width + scrollBarWidth
    popupWidth > scrollPane.getPreferredSize.width
  }

  override def popupMenuCanceled(e: PopupMenuEvent): Unit = {}

  // In its normal state the scrollpane does not have a scrollbar
  override def popupMenuWillBecomeInvisible(e: PopupMenuEvent): Unit = {
    if (scrollPane != null) scrollPane.setHorizontalScrollBar(null)
  }
}