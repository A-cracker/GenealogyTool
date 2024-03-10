package com.genealogy.vo;


public class ResultVO {

	/**
	 * result : {"result":"5233375","msg":"工具逻辑安装成功"}
	 */

	private ResultBean result;

	public ResultBean getResult() {
		return result;
	}

	public void setResult(ResultBean result) {
		this.result = result;
	}

	public static class ResultBean {
		/**
		 * result : 5233375
		 * msg : 工具逻辑安装成功
		 */

		private Long result;
		private String msg;

		public Long getResult() {
			return result;
		}

		public void setResult(Long result) {
			this.result = result;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}


