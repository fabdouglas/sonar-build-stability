/*
 * Sonar Build Stability Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.buildstability.ci.bamboo;

import org.sonar.plugins.buildstability.ci.api.AbstractServer;
import org.sonar.plugins.buildstability.ci.api.Build;
import org.sonar.plugins.buildstability.ci.api.Unmarshaller;

/**
 * See <a href="http://confluence.atlassian.com/display/BAMBOO/Bamboo+REST+APIs">Bamboo REST APIs</a>.
 *
 * @author Evgeny Mandrikov
 */
public class BambooServer extends AbstractServer {
  public static final String SYSTEM = "Bamboo";
  public static final String PATTERN = "/browse/";
  private static final Unmarshaller<Build> BUILD_UNMARSHALLER = new BambooBuildUnmarshaller();

  @Override
  public String getBuildUrl(String number) {
    StringBuilder sb = new StringBuilder(getHost())
      .append("/rest/api/latest/result/").append(getKey()).append("/").append(number)
      .append("?os_authType=basic")
      .append("&expand=results.result");
    return sb.toString();
  }

  @Override
  public String getLastBuildUrl() {
    StringBuilder sb = new StringBuilder(getHost())
      .append("/rest/api/latest/result/").append(getKey())
      .append("?os_authType=basic")
      .append("&expand=results%5B0%5D.result");
    return sb.toString();
  }

  @Override
  public Unmarshaller<Build> getBuildUnmarshaller() {
    return BUILD_UNMARSHALLER;
  }
}
