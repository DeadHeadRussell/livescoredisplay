package edu.cmu.mat.lsd.hcmp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Timer;

import org.zeromq.ZMQ;

import com.google.common.base.Joiner;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Score;
import edu.cmu.mat.scores.Section;

public class HcmpClient implements HcmpMessenger {
	private HcmpListener listener = null;

	private int join_id = (int) Math.random() * Integer.MAX_VALUE;
	private int player_id = -1;
	private long last_sync_clock = -1;
	private double offset = Double.MAX_VALUE;

	private ZMQ.Context context = ZMQ.context(1);
	private ZMQ.Socket pull = null;
	private ZMQ.Socket publish = null;

	private Timer timer = null;

	public void setListener(HcmpListener listener) {
		this.listener = listener;
	}

	public void unsetListener(HcmpListener listener) {
		if (this.listener == listener) {
			this.listener = null;
		}
	}

	public void start(String ipAddress, String portPull, String portPublish) {
		System.out.println("ZMQ starting on: " + ipAddress + " " + portPull
				+ " " + portPublish);
		connect(ipAddress, portPull, portPublish);

		final int interval = 10;
		final int sync_interval = 5000;
		final AtomicInteger counter = new AtomicInteger();
		counter.set(0);

		timer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				byte[] byte_message = pull.recv(ZMQ.DONTWAIT);

				if (byte_message != null) {
					String message = new String(byte_message);

					System.out.println("message received: " + message
							+ ", counter = " + counter.get());

					try {
						String[] tokens = message.split(" ");
						switch (tokens[0]) {
						case "hcmp":
							handleHcmpMessage(tokens);
							break;

						default:
							String[] parts = tokens[0].split("\\.");
							int id = Integer.parseInt(parts[1]);
							if (id == player_id) {
								double clock = Double.parseDouble(tokens[2]);
								offset = calculateOffset(last_sync_clock,
										new Date().getTime(), clock);
							}
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				int current = counter.incrementAndGet();
				if (sync_interval / interval == current) {
					counter.set(0);
					if (player_id == -1) {
						join();
					} else {
						synchronize();
					}
				}
			}

			private Boolean handleHcmpMessage(String[] tokens) {
				switch (tokens[1]) {
				case "id":
					return handleIdMessage(tokens);
				case "pos":
					return handlePosMessage(tokens);
				case "tm":
					return handleTmMessage(tokens);
				case "play":
					return handlePlayMessage(tokens);
				case "pause":
					return handlePauseMessage(tokens);
				case "stop":
					return handleStopMessage(tokens);
				}
				return false;
			}

			private Boolean handleIdMessage(String[] tokens) {
				int test_id = Integer.parseInt(tokens[2]);
				if (test_id == join_id) {
					player_id = Integer.parseInt(tokens[3]);
					return true;
				}
				return false;
			}

			private Boolean handlePosMessage(String[] tokens) {
				if (listener != null) {
					return listener.handleNewPosition(Integer
							.parseInt(tokens[2]));
				}
				return false;
			}

			private Boolean handleTmMessage(String[] tokens) {
				if (listener != null) {
					// double real = Double.parseDouble(tokens[2]);
					double virtual = Double.parseDouble(tokens[3]);
					// XXX: Tempo is bps, we want bpms
					double tempo = Double.parseDouble(tokens[4]) / 1000;
					double time = new Date().getTime() - offset;
					System.out.println("" + virtual + "," + tempo + "," + time);
					return listener.handleNewTime(TimeMap.Create(time, virtual,
							tempo));
				}
				return false;
			}

			private Boolean handlePlayMessage(String[] tokens) {
				if (listener != null) {
					return listener.handlePlay();
				}
				return false;
			}

			private Boolean handlePauseMessage(String[] tokens) {
				if (listener != null) {
					return listener.handlePause();
				}
				return false;
			}

			private Boolean handleStopMessage(String[] tokens) {
				if (listener != null) {
					return listener.handleStop();
				}
				return false;
			}
		});
		timer.start();
		join();
	}

	public void stop() {
		disconnect();

		if (timer != null) {
			System.out.println("ZMQ stopping");
			timer.stop();
			timer = null;
		}
	}

	private void connect(String ip_address, String port_pull,
			String port_publish) {
		disconnect();
		String address = "tcp://" + ip_address + ":";

		pull = context.socket(ZMQ.PULL);
		pull.connect(address + port_pull);
		publish = context.socket(ZMQ.PUB);
		publish.connect(address + port_publish);
	}

	private void disconnect() {
		if (pull != null) {
			pull.close();
			pull = null;
		}

		if (publish != null) {
			publish.close();
			publish = null;
		}
	}

	@Override
	public void join() {
		String message = "hcmp join " + String.valueOf(join_id) + " LSD";
		sendMessage(message);
	}

	@Override
	public void synchronize() {
		last_sync_clock = new Date().getTime();
		String message = "plr." + String.valueOf(player_id) + " resync";
		sendMessage(message);
	}

	@Override
	public void getArrangement() {
		sendMessage("hcmp get_arrangement");
	}

	@Override
	public void sendArrangement(Score score) {
		List<Section> arrangement = score.getArrangementList();
		List<String> message_parts = new ArrayList<String>(arrangement.size());

		List<Barline> start_barlines = score.getStartBarlines();
		List<Barline> end_barlines = score.getEndBarlines();

		for (Section section : arrangement) {
			int start_beat = start_barlines.indexOf(section.getStart()) * 4;
			int end_beat = end_barlines.indexOf(section.getEnd()) * 4;
			int duration = end_beat - start_beat;
			message_parts.add("(" + section.getName() + "," + start_beat + ","
					+ duration + ")");
		}
		sendMessage("hcmp arrangement " + Joiner.on(',').join(message_parts));
	}

	private void sendMessage(String message) {
		System.out.println("sending message: " + message);
		// XXX: This uses the platform's default charset. UTF-8?
		publish.send(message.getBytes(), 0);
	}

	private double calculateOffset(long start, long end, double received) {
		double current_offset = (end - start) / 2.0d;// - received;
		return Math.min(current_offset, offset);
	}
}
