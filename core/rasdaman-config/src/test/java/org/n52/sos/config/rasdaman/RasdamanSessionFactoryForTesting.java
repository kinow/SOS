/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.config.rasdaman;

import java.io.File;
import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.sos.config.rasdaman.RasdamanSessionFactory;
import org.n52.sos.ds.ConnectionProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When testing, make the sqlite config database a temp file and delete on cleanup.
 * 
 * @author Shane StClair
 *
 */
public class RasdamanSessionFactoryForTesting extends RasdamanSessionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RasdamanSessionFactoryForTesting.class); 
    protected static final String TESTING_DATABASE_NAME = "/var/hsqldb/db";
    protected static final String TESTING_CONNECTION_URL_TEMPLATE = "^jdbc:hsqldb:([:/a-zA-Z0-9]*)";    
    
    private String dbFile = "/var/hsqldb/db";
    
    @Override
    protected String getFilename() {
        return String.format(TESTING_CONNECTION_URL_TEMPLATE, "jdbc:hsqldb:file:/var/hsqldb/db");
    }
    
    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            return getSessionFactory().openSession();
        } catch (HibernateException e) {
            throw new ConnectionProviderException(e);
        }
    }
    
    @Override
    public void returnConnection(Object connection) {
        try {
            if (connection instanceof Session) {
                Session session = (Session) connection;
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
            }
        } catch (HibernateException he) {
            LOG.error("Error while returning connection!", he);
        }
    }
    
    @Override
    public void cleanup() {
    	super.cleanup();
    }    
}
