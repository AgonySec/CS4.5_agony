package aggressor.browsers;

import aggressor.AggressorClient;
import common.AObject;
import common.AdjustData;
import common.BeaconEntry;
import common.CommonUtils;
import dialog.ActivityPanel;
import dialog.DialogUtils;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import ui.ATable;
import ui.GenericTableModel;
import ui.QueryRows;
import ui.TablePopup;

public class Targets extends AObject implements AdjustData, TablePopup, QueryRows {
   protected AggressorClient client = null;
   protected ActivityPanel dialog = null;
   protected GenericTableModel model = null;
   protected ATable table = null;
   protected String[] cols = new String[]{" ", "address", "name", "note"};
   protected LinkedList targets = new LinkedList();
   protected Set compromised = new HashSet();

   public Targets(AggressorClient var1) {
      this.client = var1;
   }

   public ATable getTable() {
      return this.table;
   }

   public ActionListener cleanup() {
      return this.client.getData().unsubOnClose("targets, beacons", this);
   }

   public Map format(String var1, Object var2) {
      HashMap var3 = new HashMap((Map)var2);
      boolean var4 = this.compromised.contains((String)var3.get("address"));
      ImageIcon var5 = DialogUtils.TargetVisualizationSmall(var3.get("os") + "", CommonUtils.toDoubleNumber(var3.get("version") + "", 0.0), var4, false);
      var3.put("image", var5);
      var3.put("owned", var4 ? Boolean.TRUE : Boolean.FALSE);
      return var3;
   }

   public JComponent getContent() {
      this.client.getData().subscribe("beacons", this);
      this.targets = this.client.getData().populateListAndSubscribe("targets", this);
      this.model = DialogUtils.setupModel("address", this.cols, this.targets);
      this.table = DialogUtils.setupTable(this.model, this.cols, true);
      this.table.setPopupMenu(this);
      DialogUtils.sortby(this.table, 1);
      Map var1 = DialogUtils.toMap("address: 125, name: 125, note: 625");
      DialogUtils.setTableColumnWidths(this.table, var1);
      this.table.getColumn(" ").setPreferredWidth(32);
      this.table.getColumn(" ").setMaxWidth(32);
      DialogUtils.setupImageRenderer(this.table, this.model, " ", "image");
      DialogUtils.setupBoldOnKeyRenderer(this.table, this.model, "address", "owned");
      DialogUtils.setupBoldOnKeyRenderer(this.table, this.model, "name", "owned");
      DialogUtils.setupBoldOnKeyRenderer(this.table, this.model, "note", "owned");


      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(JLabel.CENTER);
      this.table.getColumn("address").setCellRenderer(centerRenderer);
      this.table.getColumn("name").setCellRenderer(centerRenderer);
      this.table.getColumn("note").setCellRenderer(centerRenderer);


      return DialogUtils.FilterAndScroll(this.table);
   }

   public Map[] getSelectedRows() {
      return this.model.getSelectedRows(this.table);
   }

   public void showPopup(MouseEvent var1) {
      Stack var2 = new Stack();
      var2.push(CommonUtils.toSleepArray(this.model.getSelectedValues(this.table)));
      this.client.getScriptEngine().getMenuBuilder().installMenu(var1, "targets", var2);
   }

   public void refresh() {
      this.targets = CommonUtils.apply("targets", this.targets, this);
      DialogUtils.setTable(this.table, this.model, this.targets);
   }

   public void result(String var1, Object var2) {
      if ("targets".equals(var1)) {
         this.targets = new LinkedList((LinkedList)var2);
         this.refresh();
         if (this.dialog != null) {
            this.dialog.touch();
         }
      } else if ("beacons".equals(var1)) {
         HashSet var3 = new HashSet();
         Iterator var4 = ((Map)var2).values().iterator();

         while(var4.hasNext()) {
            BeaconEntry var5 = (BeaconEntry)var4.next();
            if (var5.isActive()) {
               var3.add(var5.getInternal());
            }
         }

         if (!var3.equals(this.compromised)) {
            this.compromised = var3;
            this.refresh();
         }
      }

   }

   public boolean hasSelectedRows() {
      return this.model.hasSelectedRows(this.table);
   }

   public void notifyOnResult(ActivityPanel var1) {
      this.dialog = var1;
   }
}
