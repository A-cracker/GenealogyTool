package com.genealogy.util;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * source到target无路径，返回长度为1的数组，元素为target
 * source等于target，返回长度为2的数组，元素为target，source
 * source到target有路径，返回长度为n的数组，元素为target,...,source
 * 
 * @author zzb
 *
 */
public class PathUtil {
	/**
	 * 邻接矩阵--dijkstra
	 * @param source
	 * @param target
	 * @param graph
	 * @param MIN
	 * @return
	 */
	public static int[] dijkstra(int source,int target,int[][] graph,int MIN) {
        int[] path = null;
        int NUM = graph.length;
        // 前驱节点数组
        int[] prenode = new int[NUM];
        // 最短距离数组
        int[] mindist = new int[NUM];
        // 该节点是否已经找到最短路径
        boolean[] find = new boolean[NUM];
        int vnear = 0;
        for (int i = 0; i < mindist.length; i++) {
            prenode[i] = source;
        	mindist[i] = source<i?graph[i][source]:graph[source][i];
            find[i] = false;
        }
        find[source] = true;
        for (int v = 1; v < graph.length; v++) {
            // 每次循环求得距离vs最近的节点vnear和最短距离min
            int min = MIN;
            for (int j = 0; j < graph.length; j++) {
//            	System.out.print(mindist[j]+" "+find[j]+";");
                if (!find[j] && mindist[j] < min) {
                    min = mindist[j];
                    vnear = j;
                }
            }
//            System.out.println();
//            System.out.println("==:"+vnear);
            find[vnear] = true;
 
            // 根据vnear修正vs到其他所有节点的前驱节点及距离
            for (int k = 0; k < graph.length; k++) {
                if (!find[k] && (min + (vnear<k?graph[k][vnear]:graph[vnear][k])) < mindist[k]) {
                    prenode[k] = vnear;
                    mindist[k] = min + (vnear<k?graph[k][vnear]:graph[vnear][k]);
                }
            }
            //!!找到目标的路径则终止循环
            if(vnear == target) {
            	break;
            }
        }
        //打印路径
//        for (int i = 0; i < NUM; i++) {
//            System.out.println("v" + vs + "...v" + prenode[i] + "->v" + i + ", s=" + mindist[i]);
//            int n = i;
//            System.out.print(i+"->");
//            while(n!=vs){
//            	System.out.print(prenode[n]+"->");
//            	n = prenode[n];
//            }
//            System.out.println();
//        }
        if(mindist[target]==MIN) {
        	path = new int[1];
        	path[0] = target;
        }else {
        	path = new int[mindist[target]+1];
        	path[0] = target;
        	int n = target;
        	for(int i=1;i<path.length;i++) {
        		path[i] = prenode[n];
        		n = prenode[n];
        	}
        }
        return path;
    }
	
	/**
	 * 邻接表--dijkstra
	 * @param source
	 * @param target
	 * @param graph
	 * @param MIN
	 * @return
	 */
	public static long[] dijkstra(int source,int target,LinkedList<Integer>[] graph,int MIN) {
		long[] path = null;
		int NUM = graph.length;
        // 前驱节点数组
        int[] prenode = new int[NUM];
        // 最短距离数组
        int[] mindist = new int[NUM];
        // 该节点是否已经找到最短路径
        boolean[] find = new boolean[NUM];
        int vnear = 0;
        for (int i = 0; i < mindist.length; i++) {
            prenode[i] = source;
        	mindist[i] = MIN;
            find[i] = false;
        }
        LinkedList<Integer> list = graph[source];
        Iterator<Integer> iterator = list.iterator();
        while(iterator.hasNext()) {
        	int temp = iterator.next();
        	mindist[temp] = 1;
        }
        find[source] = true;

        for (int v = 1; v < NUM; v++) {
            // 每次循环求得距离vs最近的节点vnear和最短距离min
            int min = MIN;
            
            for (int j = 0; j < NUM; j++) {
                if (!find[j] && mindist[j] < min) {
                    min = mindist[j];
                    vnear = j;
                }
            }
            find[vnear] = true;
 
            // 根据vnear修正vs到其他所有节点的前驱节点及距离
            list = graph[vnear];
            iterator = list.iterator();
            while(iterator.hasNext()) {
            	int temp = iterator.next();
            	if(!find[temp]&& min+1 < mindist[temp]) {
            		prenode[temp] = vnear;
            		mindist[temp] = min + 1;
            	}
            }
            //!!找到目标的路径则终止循环
            if(vnear == target) {
            	break;
            }
        }
		
		if(mindist[target]==MIN) {
		    //目标成员与起始成员没有关系的情况
        	path = new long[1];
        	path[0] = target;
        }else {
        	path = new long[mindist[target]+1];
        	path[0] = target;
        	int n = target;
        	for(int i=1;i<path.length;i++) {
        		path[i] = prenode[n];
        		n = prenode[n];
        	}
        }
		return path;
	}
}
