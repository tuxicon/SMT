/*
 * Modified version of TUIO Simulator - part of the reacTIVision project
 * http://reactivision.sourceforge.net/
 * 
 * Copyright (c) 2005-2009 Martin Kaltenbrunner <mkalten@iua.upf.edu>
 * 
 * This version Copyright (c) 2011 Erik Paluka, Christopher Collins - University
 * of Ontario Institute of Technology Mark Hancock - University of Waterloo
 * contact: christopher.collins@uoit.ca
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 3 as published by the
 * Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package vialab.TUIOSource;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import processing.core.PConstants;

import processing.event.MouseEvent;

/**
 * This class responds to mouse events by sending a corresponding TUIO message.
 * <P>
 * 
 * University of Ontario Institute of Technology. Summer Research Assistant with
 * Dr. Christopher Collins (Summer 2011) collaborating with Dr. Mark Hancock.
 * <P>
 * 
 * @author Erik Paluka
 * @author Zach Cook
 * @date September, 2012
 * @version 2.0
 */
public class MouseToTUIO {

	private static Simulation sim;

	/** Currently selected cursor */
	Finger selectedCursor = null;

	/** Vector of the sticky cursors */
	Vector<Integer> stickyCursors = new Vector<Integer>();
	/** 
	 * Vector of the joint cursors.
	 * <P> These cursors  are meant to move as a group.
	 */
	Vector<Integer> jointCursors = new Vector<Integer>();

	/**
	 * Sets up everything to get ready to begin converting mouse events to TUIO
	 * messages. Sets the screen to be full-screen if the boolean/flag is set to
	 * true.
	 * 
	 * @param applet
	 *            PApplet - Processing PApplet
	 */
	public MouseToTUIO(int width, int height) {
		super();
		sim = new Simulation(width, height);
	}

	/**
	 * Updates the selected cursor
	 * 
	 * @param me
	 *            MouseEvent - The mouse dragged event
	 */
	public void mouseDragged(MouseEvent me) {
		Point pt = new Point(me.getX(), me.getY());

		if (selectedCursor != null) {
			if (sim.contains(pt)) {
				if (jointCursors.contains(selectedCursor.sessionID)) {
					Point selPoint = selectedCursor.getPosition();
					int dx = pt.x - selPoint.x;
					int dy = pt.y - selPoint.y;

					for (int jointId : jointCursors) {
						if (jointId == selectedCursor.sessionID)
							continue;
						Finger joint_cursor = sim.getCursor(jointId);
						Point joint_point = joint_cursor.getPosition();
						sim.updateCursor(joint_cursor, joint_point.x + dx, joint_point.y + dy);
					}
					sim.updateCursor(selectedCursor, pt.x, pt.y);
					sim.allCursorMessage();
				}
				else {
					sim.updateCursor(selectedCursor, pt.x, pt.y);
					sim.cursorMessage(selectedCursor);
				}
			}
			else {
				selectedCursor.stop();
				sim.cursorMessage(selectedCursor);
				if (stickyCursors.contains(selectedCursor.sessionID))
					stickyCursors.removeElement(selectedCursor.sessionID);
				if (jointCursors.contains(selectedCursor.sessionID))
					jointCursors.removeElement(selectedCursor.sessionID);
				sim.removeCursor(selectedCursor);
				selectedCursor = null;
			}
		}
		else {
			if (sim.contains(pt)) {
				selectedCursor = sim.addCursor(pt.x, pt.y);
				sim.cursorMessage(selectedCursor);
				if (me.isShiftDown() || me.getButton() == PConstants.RIGHT)
					stickyCursors.addElement(selectedCursor.sessionID);
			}
		}
	}

	/**
	 * Adds a cursor to the table state
	 * 
	 * @param me
	 *            MouseEvent - The mouse pressed event
	 */
	public void mousePressed(MouseEvent me) {
		int x = me.getX();
		int y = me.getY();

		Enumeration<Finger> cursorList = Simulation.cursorList.elements();
		while (cursorList.hasMoreElements()) {
			Finger cursor = cursorList.nextElement();
			Point point = cursor.getPosition();

			if (point.distance(x, y) < 20) {
				int selCur = -1;
				if (selectedCursor != null)
					selCur = selectedCursor.sessionID;

				if ((me.isShiftDown() || me.getButton() == PConstants.RIGHT)
						&& selCur != cursor.sessionID) {
					stickyCursors.removeElement(cursor.sessionID);
					if (jointCursors.contains(cursor.sessionID))
						jointCursors.removeElement(cursor.sessionID);
					sim.removeCursor(cursor);
					selectedCursor = null;
					return;
				}
				else if (me.isControlDown() || me.getButton() == PConstants.CENTER) {
					if (jointCursors.contains(cursor.sessionID))
						jointCursors.removeElement(cursor.sessionID);
					else
						jointCursors.addElement(cursor.sessionID);
					return;
				}
				else {
					selectedCursor = cursor;
					sim.updateCursor(selectedCursor, x, y);
					sim.cursorMessage(selectedCursor);
					return;
				}
			}
		}

		if (me.isControlDown() || me.getButton() == PConstants.CENTER){
			return;
		}

		if (sim.contains(new Point(x, y))) {
			selectedCursor = sim.addCursor(x, y);
			sim.cursorMessage(selectedCursor);
			if (me.isShiftDown() || me.getButton() == PConstants.RIGHT){
				stickyCursors.addElement(selectedCursor.sessionID);
			}
			return;
		}

		selectedCursor = null;
	}

	/**
	 * Removes the cursor or makes it stationary if it is a sticky cursor
	 * 
	 * @param me
	 *            MouseEvent - The mouse released event
	 */
	public void mouseReleased(MouseEvent me) {
		if ((selectedCursor != null)) {
			if (!stickyCursors.contains(selectedCursor.sessionID)) {
				selectedCursor.stop();
				sim.cursorMessage(selectedCursor);
				if (jointCursors.contains(selectedCursor.sessionID))
					jointCursors.removeElement(selectedCursor.sessionID);
				sim.removeCursor(selectedCursor);
			}
			else {
				selectedCursor.stop();
				sim.cursorMessage(selectedCursor);
			}

			selectedCursor = null;
		}
	}

	/**
	 * Currently not used
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseMoved(MouseEvent me) {
	}

	/**
	 * Currently not used
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseClicked(MouseEvent me) {
	}

	/**
	 * Currently not used
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseEntered(MouseEvent me) {
	}

	/**
	 * Will release the Touch when the mouse exits the window
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseExited(MouseEvent me) {
		//prevent touches getting stuck down by making them release on exit
		this.mouseReleased(me);
	}

	public void mouseEvent(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			mousePressed(event);
			break;
		case MouseEvent.RELEASE:
			mouseReleased(event);
			break;
		case MouseEvent.CLICK:
			mouseClicked(event);
			break;
		case MouseEvent.DRAG:
			mouseDragged(event);
			break;
		case MouseEvent.MOVE:
			mouseMoved(event);
			break;
		case MouseEvent.ENTER:
			mouseEntered(event);
			break;
		case MouseEvent.EXIT:
			mouseExited(event);
			break;
		}
	}

	/**
	 * Resets MouseToTUIO state, just making the already available functionality
	 * visible
	 */
	public void reset() {
		sim.reset();
		stickyCursors.clear();
		jointCursors.clear();
	}
	
	public Integer[] getJointCursors(){
		return jointCursors.toArray(new Integer[jointCursors.size()]);
	}
}
