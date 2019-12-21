package com.fbi.engine.plugins.hello;

import java.io.IOException;
import java.io.Writer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fbi.engine.api.Query;
import com.fbi.engine.api.QueryExecutor;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public FlairCompiler compiler() {
		return new FlairCompiler() {

			public void compile(FlairQuery input, Writer writer) throws CompilationException {
				try {
					writer.write("Hello this is hello plugin");
				} catch (IOException e) {
					throw new CompilationException(e);
				}
			}
		};
	}

	@Bean
	public QueryExecutorFactory executor() {
		return (conn, driver) -> {
			return new QueryExecutor() {

				public void execute(Query input, Writer writer) throws ExecutionException {
					try {
						writer.write("Hello this is hello plugin query executor");
					} catch (IOException e) {
						throw new ExecutionException(e);
					}

				}
			};
		};
	}

}
