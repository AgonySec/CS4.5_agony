package aggressor.browsers;

import aggressor.AggressorClient;
import aggressor.DataUtils;
import common.AObject;
import common.Callback;
import dialog.DialogUtils;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import ui.ATable;
import ui.GenericTableModel;
import ui.TablePopup;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
public class Sessions extends AObject implements Callback, TablePopup {
   protected AggressorClient client = null;
   protected GenericTableModel model = null;
   protected ATable table = null;
   protected String[] cols = new String[]{" ", "external", "internal", "address","listener", "user", "computer", "note", "process", "pid", "arch", "last"};
   protected boolean multipleSelect;

   public ATable getTable() {
      return this.table;
   }

   public void setColumns(String[] var1) {
      this.cols = var1;
   }

   public Sessions(AggressorClient var1, boolean var2) {
      this.client = var1;
      this.multipleSelect = var2;
   }

   public ActionListener cleanup() {
      return this.client.getData().unsubOnClose("beacons", this);
   }

   public WindowListener onclose() {
      return this.client.getData().unsubOnClose("beacons", this);
   }

   public boolean hasSelectedRows() {
      return this.model.hasSelectedRows(this.table);
   }

   public Object[] getSelectedValues() {
      return this.model.getSelectedValues(this.table);
   }

   public Object getSelectedValue() {
      return this.model.getSelectedValue(this.table) + "";
   }

   public void showPopup(MouseEvent var1) {
      DialogUtils.showSessionPopup(this.client, var1, this.model.getSelectedValues(this.table));
   }

   public JComponent getContent() {
      if (this.cols.length == 11) {
         this.model = DialogUtils.setupModel("id", this.cols, DataUtils.getBeaconModel(this.client.getData()));
      } else {
         this.model = DialogUtils.setupModel("id", this.cols, DataUtils.getBeaconModel(this.client.getData()));
      }

      this.table = DialogUtils.setupTable(this.model, this.cols, this.multipleSelect);
      if (this.cols.length == 11) {
         DialogUtils.sortby(this.table, 2, 8);
         this.table.getColumn("arch").setPreferredWidth(96);
         this.table.getColumn("arch").setMaxWidth(96);
      } else {
         DialogUtils.sortby(this.table, 1);
      }
      // repair session show
      this.table.getColumnModel().getColumns().asIterator().forEachRemaining(new Consumer<TableColumn>() {
         public void accept(TableColumn tableColumn) {
            tableColumn.setCellRenderer(new TableCellRenderer() {
               public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                  Component tableCellRendererComponent = table.getDefaultRenderer(String.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                  ((JLabel) tableCellRendererComponent).setIcon(null);
                  ((JLabel) tableCellRendererComponent).setHorizontalAlignment(JLabel.CENTER); // 设置水平居中对齐
                  return tableCellRendererComponent;
               }
            });
         }
      });
      this.table.getColumn(" ").setPreferredWidth(32);
      this.table.getColumn(" ").setMaxWidth(32);
      this.table.getColumn("pid").setPreferredWidth(96);
      this.table.getColumn("pid").setMaxWidth(96);
      this.table.getColumn("last").setPreferredWidth(96);
      this.table.getColumn("last").setMaxWidth(96);
      DialogUtils.setupImageRenderer(this.table, this.model, " ", "image");
      DialogUtils.setupTimeRenderer(this.table, "last");
      this.table.setPopupMenu(this);
      this.client.getData().subscribe("beacons", this);
      return DialogUtils.FilterAndScroll(this.table);
   }

   public void result(String var1, Object var2) {
      if (this.table.isShowing()) {
         DialogUtils.setTable(this.table, this.model, DataUtils.getBeaconModelFromResult(var2));
      }
   }
}
