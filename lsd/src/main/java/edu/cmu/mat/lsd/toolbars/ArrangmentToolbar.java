package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import com.google.common.base.Joiner;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;

public class ArrangmentToolbar implements Toolbar, ControllerListener {
	private Model _model;
	private JToolBar _toolbar = new JToolBar("ArrangmentTools");
	private JList<String> _list;
	private DefaultListModel<String> _list_model;

	public ArrangmentToolbar(Model model) {
		_model = model;
		_toolbar.setBackground(new Color(220, 220, 220));

		_list_model = new DefaultListModel<String>();
		_list = new JList<String>(_list_model);
		_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		_list.setVisibleRowCount(-1);
		_list.setDragEnabled(true);
		_list.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 7219438894474248914L;

			private int[] indices;

			public boolean canImport(TransferHandler.TransferSupport info) {
				if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					return false;
				}
				return true;
			}

			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				JList.DropLocation dl = (JList.DropLocation) info
						.getDropLocation();
				int index = dl.getIndex();
				boolean insert = dl.isInsert();

				Transferable t = info.getTransferable();
				String data;
				try {
					data = (String) t.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					return false;
				}

				String[] values = data.split("\n");
				for (int i = 0; i < values.length; i++) {
					if (insert) {
						_list_model.add(index++, values[i]);
					} else {
						if (index < _list_model.getSize()) {
							_list_model.set(index++, values[i]);
						} else {
							_list_model.add(index++, values[i]);
						}
					}
				}

				_model.saveArrangment(_list_model);

				return true;
			}

			public int getSourceActions(JComponent c) {
				return COPY_OR_MOVE;
			}

			public Transferable createTransferable(JComponent c) {
				indices = _list.getSelectedIndices();
				List<String> values = _list.getSelectedValuesList();
				return new StringSelection(Joiner.on("\n").join(values));
			}

			public void exportDone(JComponent c, Transferable t, int action) {
				if (action == MOVE) {
					for (int i = indices.length - 1; i >= 0; i--) {
						_list_model.remove(indices[i]);
					}
				}
				indices = null;

				_model.saveArrangment(_list_model);
			}
		});

		JScrollPane listScroller = new JScrollPane(_list);
		// listScroller.setPreferredSize(new Dimension(250, 80));

		_list.setDropMode(DropMode.ON_OR_INSERT);
		_toolbar.add(listScroller);
		onUpdateScore();
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

	public void onUpdateModel() {
	}

	public void onUpdateScore() {
		_model.loadArrangment(_list_model);
		_list.invalidate();
	}

	public void onUpdateLibraryPath() {
	}

	public void onUpdateView() {
	}

	public void onUpdateTool() {
	}

	public void onProgramQuit() {
	}
}
