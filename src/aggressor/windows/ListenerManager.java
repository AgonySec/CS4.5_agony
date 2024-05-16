package aggressor.windows;

import aggressor.AggressorClient;
import aggressor.ColorManager;
import aggressor.DataManager;
import aggressor.DataUtils;
import aggressor.dialogs.ScListenerDialog;
import common.AObject;
import common.AdjustData;
import common.BeaconEntry;
import common.Callback;
import common.CommonUtils;
import common.ListenerTasks;
import common.TeamQueue;
import cortana.Cortana;
import dialog.DialogUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import javax.swing.*;

import ui.ATable;
import ui.GenericTableModel;
import ui.QueryableTable;
import ui.TablePopup;
import javax.swing.table.DefaultTableCellRenderer;
public class ListenerManager extends AObject implements AdjustData, Callback, ActionListener, TablePopup {
   protected TeamQueue conn = null;
   protected Cortana engine = null;
   protected DataManager data = null;
   protected AggressorClient client = null;
   protected GenericTableModel model = null;
   protected ATable table = null;
   protected String[] cols = new String[]{"name", "payload", "host", "port", "bindto", "beacons", "profile"};

   public ListenerManager(AggressorClient var1) {
      this.engine = var1.getScriptEngine();
      this.conn = var1.getConnection();
      this.data = var1.getData();
      this.client = var1;
      this.model = DialogUtils.setupModel("name", this.cols, CommonUtils.apply("listeners", DataUtils.getListenerModel(this.data), this));
      this.data.subscribe("listeners", this);
   }

   public ActionListener cleanup() {
      return this.data.unsubOnClose("listeners", this);
   }

   public void actionPerformed(ActionEvent var1) {
      if ("Add".equals(var1.getActionCommand())) {
         (new ScListenerDialog(this.client)).show();
      } else if ("Edit".equals(var1.getActionCommand())) {
         if (!this.model.hasSelectedRows(this.table)) {
            DialogUtils.showError(DialogUtils.MessageID.A_ROW_MUST_BE_SELECTED);
            return;
         }

         String var2 = this.model.getSelectedValue(this.table) + "";
         (new ListenerTasks(this.client, var2)).edit();
      } else {
         int var3;
         Object[] var4;
         if ("Remove".equals(var1.getActionCommand())) {
            var4 = this.model.getSelectedValues(this.table);
            if (var4.length == 0) {
               DialogUtils.showError(DialogUtils.MessageID.ROWS_MUST_BE_SELECTED);
               return;
            }

            for(var3 = 0; var3 < var4.length; ++var3) {
               (new ListenerTasks(this.client, (String)var4[var3])).remove();
            }
         } else if ("Restart".equals(var1.getActionCommand())) {
            var4 = this.model.getSelectedValues(this.table);
            if (var4.length == 0) {
               DialogUtils.showError(DialogUtils.MessageID.ROWS_MUST_BE_SELECTED);
               return;
            }

            for(var3 = 0; var3 < var4.length; ++var3) {
               this.conn.call("listeners.restart", CommonUtils.args(var4[var3]), new Callback() {
                  public void result(String var1, Object var2) {
                     if (var2 != null) {
                        DialogUtils.showInfo("Updated and restarted listener: " + var2);
                     }

                  }
               });
            }
         }
      }

   }

   public void showPopup(MouseEvent var1) {
      JPopupMenu var2 = new JPopupMenu();
      JMenu var3 = new JMenu("Color");
      var3.add((new ColorManager(this.client, new QueryableTable(this.table, this.model), "listeners")).getColorPanel());
      var2.add(var3);
      Object[] var4 = this.model.getSelectedValues(this.table);
      Stack var5 = new Stack();
      var5.push(CommonUtils.toSleepArray(var4));
      this.client.getScriptEngine().getMenuBuilder().setupMenu(var2, "listeners", var5);
      var2.show((Component)var1.getSource(), var1.getX(), var1.getY());
   }

   public JComponent getContent() {
      JPanel var1 = new JPanel();
      var1.setLayout(new BorderLayout());
      this.table = DialogUtils.setupTable(this.model, this.cols, true);

      // Add the cell renderer
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(JLabel.CENTER);
      for (int i = 0; i < this.table.getColumnCount(); i++) {
         this.table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
      }

      this.table.setPopupMenu(this);
      DialogUtils.setupListenerStatusRenderer(this.table, this.model, "name");
      DialogUtils.setTableColumnWidths(this.table, DialogUtils.toMap("name: 125, payload: 250, host: 125, port: 60, bindto: 60, beacons: 250, profile: 125"));
      DialogUtils.sortby(this.table, 0);
      JButton var2 = new JButton("Add");
      JButton var3 = new JButton("Edit");
      JButton var4 = new JButton("Remove");
      JButton var5 = new JButton("Restart");
      JButton var6 = new JButton("Help");
      var2.addActionListener(this);
      var3.addActionListener(this);
      var4.addActionListener(this);
      var5.addActionListener(this);
      var6.addActionListener(DialogUtils.gotoURL("https://www.cobaltstrike.com/help-listener-management"));
      var1.add(DialogUtils.FilterAndScroll(this.table), "Center");
      var1.add(DialogUtils.center(var2, var3, var4, var5, var6), "South");
      return var1;
   }

   public Map format(String var1, Object var2) {
      Map var3 = (Map)var2;
      String var4 = DialogUtils.string(var3, "bid");
      String var5 = DialogUtils.string(var3, "payload");
      if (!"".equals(var4) && "windows/beacon_reverse_tcp".equals(var5)) {
         BeaconEntry var6 = DataUtils.getBeacon(this.data, var4);
         if (var6 == null) {
            var3.put("status", "pivot session does not exist");
         } else if (!var6.isAlive()) {
            var3.put("status", "pivot session is not alive");
         }

         return var3;
      } else {
         return var3;
      }
   }

   public void result(String var1, Object var2) {
      LinkedList var3 = CommonUtils.apply(var1, ((Map)var2).values(), this);
      DialogUtils.setTable(this.table, this.model, var3);
   }
}
