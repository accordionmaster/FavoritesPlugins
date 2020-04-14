package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class HelloWorld {

	public static void main(String[] args) {
		/**
		 * 每一个基于SWT的应用程序只有一个Display实例。该实例表示了底层平台和SWT接口间的连接。
		 * 除了可以管理SWT事件之外，它还提供访问SWT所需的系统资源的方法。
		 */
		Display display = new Display();
		/**
		 * 每个窗口有一个Shell对象，他代表用户交互的窗口帧。他处理对所有窗口都类似的移动和变换大小的 行为，并且它是显示在他边框内的所有窗口小部件的父对象。
		 */
		Shell shell = new Shell(display);
		/**
		 * 设置窗口帧的标题
		 */
		shell.setText("Hello World");
		/**
		 * 用于窗口帧的大小和位置。本例中，窗口帧将是200像素宽，50像素高，并位于距离屏幕 左上角100*100像素的位置。
		 */
		shell.setBounds(100, 100, 200, 50);
		/**
		 * 设置窗口布局，FillLayout是简单的布局，他让单一子窗口小部件填充父窗口小部件的整个边框。
		 */
		shell.setLayout(new FillLayout());
		/**
		 * 创建简单标签部件，该部件的父部件是shell，并在相对与其自身的中央位置显示它的文本。
		 */
		Label label = new Label(shell, SWT.CENTER);
		label.setText("Hello World");
		Color redColor = new Color(null, 255, 0, 0);
		label.setForeground(redColor);
		/**
		 * open方法让窗口帧显示
		 */
		shell.open();
		/**
		 * while循环一直检查窗口帧是否被关闭
		 */
		while (!shell.isDisposed()) {
			// readAndDispatch方法从系统时间对垒读取事件，并将他们分发至适合的接受者。
			// 当还有更多任务要完成时，方法返回true，当事件队列为空时，返回false
			// （此时可以运行UI线程睡眠，直至有更多的任务要完成）
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		redColor.dispose();
		display.dispose();
	}
}
