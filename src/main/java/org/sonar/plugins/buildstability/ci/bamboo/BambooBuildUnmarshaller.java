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

import org.apache.commons.lang.time.DateUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.buildstability.ci.api.Build;
import org.sonar.plugins.buildstability.ci.api.Status;
import org.sonar.plugins.buildstability.ci.api.Unmarshaller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Evgeny Mandrikov
 */
public class BambooBuildUnmarshaller implements Unmarshaller<Build> {
  private static final String SUCCESSFULL = "Successful";
  private static final Logger LOG = LoggerFactory.getLogger(BambooBuildUnmarshaller.class);

  /**
   * Bamboo date-time format. Example: 2010-01-04T11:02:17.114-0600
   */
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  @Override
  public Build toModel(Element rootElement) {
    Build build = new Build();

    Element result;
    Element results = rootElement.element("results");
    if (results != null) {
      result = results.element("result");
    } else {
      result = rootElement;
    }
    if (result == null) {
      return null;
    }

    String state = result.attributeValue("state");
    build.setNumber(Integer.parseInt(result.attributeValue("number")));

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
    String buildStartedTime = result.elementText("buildStartedTime");
    // Remove ':' in the timezone because it is not was the Java format expect
    int timezoneColonSeparatorIndex = buildStartedTime.lastIndexOf(':');
    if (timezoneColonSeparatorIndex == buildStartedTime.length() -3) {
      buildStartedTime = buildStartedTime.substring(0, timezoneColonSeparatorIndex) + buildStartedTime.substring(timezoneColonSeparatorIndex + 1);
    }
    try {
      Date date = sdf.parse(buildStartedTime);
      build.setTimestamp(date.getTime());
    } catch (ParseException ignored) {
      LOG.warn("Unable to parse date {}. Expected format is {}", buildStartedTime, DATE_TIME_FORMAT);
    }
    build.setDuration((long) (Double.parseDouble(result.elementText("buildDurationInSeconds")) * DateUtils.MILLIS_PER_SECOND));
    build.setStatus(SUCCESSFULL.equalsIgnoreCase(state) ? Status.success : Status.failed);

    return build;
  }
}
