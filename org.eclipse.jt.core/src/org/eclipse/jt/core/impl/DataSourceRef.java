package org.eclipse.jt.core.impl;

import java.sql.SQLException;

import org.eclipse.jt.core.misc.SXElement;


/**
 * 数据库连接信息
 * 
 * @author Jeff Tang
 * 
 */
final class DataSourceRef {
	// oracle url
	// jdbc:oracle:thin:@hostname:1521:sid

	// sqlserver url
	// jdbc:sqlserver://hostname:1433;databasename=databasename

	// db2 url
	// jdbc:db2://hostname:50000/databasename

	// mysql url
	// jdbc:mysql://hostname:3306/databasename?allowMultiQueries=true

	final String catalog;
	final DataSourceImpl dataSource;

	final DBConnectionEntry allocDBConnectionEntry() throws SQLException {
		return this.dataSource.alloc(this);
	}

	final DBLang getLang() throws SQLException {
		return this.dataSource.getLang();
	}

	// ///////////////////////////////////////////////////////////
	// /////// XML
	// ///////////////////////////////////////////////////////////
	final static String xml_element_datasourceref = "datasource-ref";
	final static String xml_attr_space = "space";
	final static String xml_attr_catalog = "catalog";
	final static String xml_attr_datasource_author = "datasource-author";
	final static String xml_attr_datasource = "datasource";

	DataSourceRef(DataSourceManager manager, SXElement element) {
		String dataSourceName = element.getAttribute(xml_attr_datasource, null);
		String dataSourceAuthor = dataSourceName == null ? null : element
		        .getAttribute(xml_attr_datasource_author, null);
		this.dataSource = manager.getDataSource(dataSourceAuthor,
		        dataSourceName);
		this.catalog = element.getAttribute(xml_attr_catalog, null);
	}

	DataSourceRef(DataSourceImpl datasource) {
		this.dataSource = datasource;
		this.catalog = null;
	}
}
