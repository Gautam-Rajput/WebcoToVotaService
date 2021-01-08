package com.webco.Model;

import java.util.List;

public class FinalResponse {

	private long total_rows;
	private int offset;
	private List<RowModel> rows;
	public long getTotal_rows() {
		return total_rows;
	}
	public void setTotal_rows(long total_rows) {
		this.total_rows = total_rows;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public List<RowModel> getRows() {
		return rows;
	}
	public void setRows(List<RowModel> rows) {
		this.rows = rows;
	}
	
	
}
