package com.genealogy.vo;


/**
 * 各类申请的统计，只统计申请中的记录
 * @author ljtian
 *
 */
public class ApplyingCount {
	/**
	 * 教师资格申请中的数量
	 */
	private Long teacher;
	/**
	 * 教师任课申请中的数量
	 */
	private Long teach;
	/**
	 * 学生听课申请中的数量
	 */
	private Long student;
	/**
	 * 课程上架申请中的数量
	 */
	private Long course;

	private Long allCourse;
	public Long getTeacher() {
		return teacher;
	}
	public void setTeacher(Long teacher) {
		this.teacher = teacher;
	}
	public Long getTeach() {
		return teach;
	}
	public void setTeach(Long teach) {
		this.teach = teach;
	}
	public Long getStudent() {
		return student;
	}
	public void setStudent(Long student) {
		this.student = student;
	}
	public Long getCourse() {
		return course;
	}
	public void setCourse(Long course) {
		this.course = course;
	}


	public Long getAllCourse() {
		return allCourse;
	}

	public void setAllCourse(Long allCourse) {
		this.allCourse = allCourse;
	}
}
