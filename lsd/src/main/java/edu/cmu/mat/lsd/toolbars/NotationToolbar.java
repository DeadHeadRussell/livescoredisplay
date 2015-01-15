package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.base.Joiner;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;

public class NotationToolbar implements Toolbar, ControllerListener {
	private Model _model;
	private JToolBar _toolbar = new JToolBar("NotationTools");
	private JList<String> _list;
	private DefaultListModel<String> _list_model;
	private JList<String> _arrange_list;
	private DefaultListModel<String> _arrange_list_model;

	public NotationToolbar(Model model) {
		_model = model;

		_toolbar.setBackground(new Color(220, 220, 220));

		JButton newButton = new JButton("New...");
		JButton moveButton = new JButton("Move");
		JButton deleteButton = new JButton("Delete");

		JButton previousButton = new JButton("Previous");
		JButton centerButton = new JButton("Center");
		JButton nextButton = new JButton("Next");

		_toolbar.add(newButton);
		_toolbar.add(moveButton);
		_toolbar.add(deleteButton);

		_toolbar.addSeparator();

		_toolbar.add(previousButton);
		_toolbar.add(centerButton);
		_toolbar.add(nextButton);

		_toolbar.addSeparator();

		_list_model = new DefaultListModel<String>();
		_list = new JList<String>(_list_model);
		_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		_list.setVisibleRowCount(-1);
		_list.setDragEnabled(true);
		JScrollPane listScroller = new JScrollPane(_list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		_toolbar.add(listScroller);

		_arrange_list_model = new DefaultListModel<String>();
		_arrange_list = new JList<String>(_arrange_list_model);
		_arrange_list
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		_arrange_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		_arrange_list.setVisibleRowCount(-1);
		_arrange_list.setDragEnabled(true);
		_arrange_list.setTransferHandler(new TransferHandler() {
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
						_arrange_list_model.add(index++, values[i]);
					} else {
						if (index < _arrange_list_model.getSize()) {
							_arrange_list_model.set(index++, values[i]);
						} else {
							_arrange_list_model.add(index++, values[i]);
						}
					}
				}

				_model.saveArrangment(_arrange_list_model);

				return true;
			}

			public int getSourceActions(JComponent c) {
				return COPY_OR_MOVE;
			}

			public Transferable createTransferable(JComponent c) {
				indices = _arrange_list.getSelectedIndices();
				List<String> values = _arrange_list.getSelectedValuesList();
				return new StringSelection(Joiner.on("\n").join(values));
			}

			public void exportDone(JComponent c, Transferable t, int action) {
				if (action == MOVE) {
					for (int i = indices.length - 1; i >= 0; i--) {
						_arrange_list_model.remove(indices[i]);
					}
				}
				indices = null;

				_model.saveArrangment(_arrange_list_model);
			}
		});

		JScrollPane arrangeList = new JScrollPane(_arrange_list);
		// listScroller.setPreferredSize(new Dimension(250, 80));
		_arrange_list.setDropMode(DropMode.ON_OR_INSERT);
		_toolbar.add(arrangeList);

		JButton saveArrangementButton = new JButton("Save Arrangement");
		_toolbar.add(saveArrangementButton);

		onUpdateScore();

		final JPopupMenu newPopupMenu = new JPopupMenu();

		newPopupMenu.add(new JMenuItem(new AbstractAction("System") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_SYSTEM_TOOL);
			}
		}));

		newPopupMenu.add(new JMenuItem(new AbstractAction("Barline") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_BARLINE_TOOL);
			}
		}));

		newPopupMenu.add(new JMenuItem(new AbstractAction("Section") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_SECTION_TOOL);
			}
		}));

		newButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				newPopupMenu.show(event.getComponent(), event.getX(),
						event.getY());
			}
		});

		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.MOVE_TOOL);
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.DELETE_TOOL);
			}
		});

		saveArrangementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.sendArrangement();
			}
		});

		ListSelectionListener selectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {

				} else {

				}
			}
		};

		_list.addListSelectionListener(selectionListener);
		_arrange_list.addListSelectionListener(selectionListener);
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

	@Override
	public void onUpdateLibraryPath() {
	}

	@Override
	public void onUpdateModel() {
		_model.loadSections(_list_model);
		_list.invalidate();
		_model.loadArrangment(_arrange_list_model);
		_arrange_list.invalidate();
	}

	@Override
	public void onUpdateScore() {
		onUpdateModel();
	}

	@Override
	public void onUpdateView() {
	}

	@Override
	public void onUpdateTool() {
	}

	@Override
	public void onProgramQuit() {
	}
}
