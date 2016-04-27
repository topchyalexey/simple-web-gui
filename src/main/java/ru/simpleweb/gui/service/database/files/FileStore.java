package ru.simpleweb.gui.service.database.files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.ejb.Singleton;

import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;


@Singleton(mappedName="FileStore")
public class FileStore {
	
	private static final RowMapper<byte[]> ROW_MAPPER_BYTES = new RowMapper<byte[]>() {
		@Override
		public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
			try {
				return IOUtils.toByteArray(rs.getBinaryStream(1));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	JdbcTemplate jdbc;
	/**
	 * synchronized вместе с @Singleton обеспечивают последовательный доступ к БД, когда бины пытаются сохранять файлы параллельно
	 * (при попытке параллельного сохранения возникает DATABASE_BUSY)
	 */
	public synchronized Long save(final byte[] bytes, final String fileName) {
		final String uuid = UUID.randomUUID().toString();
		getJdbc().update("insert into files_content (file_name,value, uuid) values (?, ?, ?)", new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, fileName);
				//InputStream baos = new ByteArrayInputStream(bytes);
				ps.setBytes(2, bytes );
				ps.setString(3, uuid);
			}
		});
		return getIdByUID(uuid);
	}

	public long getIdByUID(final String uuid) {
		return getJdbc().queryForLong("select id from files_content where uuid = ?", uuid);
	}

	public byte[] getByID(final Long id) {
		return getJdbc().queryForObject("select value from files_content where id = ?",	new Object[] {id } , ROW_MAPPER_BYTES);
	}
	
	private JdbcTemplate getJdbc() {
		return jdbc;
	}

	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public byte[] getLastByFileName(String name) {
		return getJdbc().queryForObject("select value from FILES_CONTENT where id = ?", new Object[]{getLastIdForFileName(name) }, ROW_MAPPER_BYTES);
	}

	public Long getLastIdForFileName(String name) {
		return getJdbc().queryForObject("select max(id) from FILES_CONTENT where file_name = ?", new Object[]{name }, Long.class);
	}

}
