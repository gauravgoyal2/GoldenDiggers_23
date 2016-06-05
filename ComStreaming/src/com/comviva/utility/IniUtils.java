package com.comviva.utility;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;



public class IniUtils
{

	/** The default case sensitivity. */
	private static boolean defaultCaseSensitivity = false;

	/** The sections. */
	private Map<String, Section> sections;

	/** The section order. */
	private List<String> sectionOrder;

	/** The common name. */
	private String commonName;

	/** The comment delims. */
	private char[] commentDelims;

	/** The is case sensitive. */
	private boolean isCaseSensitive;

	/**
	 * Constructs new bare IniUtils instance.
	 */
	public IniUtils()
	{
		this(null, null);
	}

	/**
	 * Constructs new bare IniUtils instance specifying case-sensitivity.
	 * 
	 * @param isCaseSensitive section and option names are case-sensitive if this is true
	 */
	public IniUtils(boolean isCaseSensitive)
	{
		this(null, null, isCaseSensitive);
	}

	/**
	 * Constructs new IniUtils instance with a common section. Options in the
	 * common section are used as defaults for all other sections.
	 * 
	 * @param commonName name of the common section
	 */
	public IniUtils(String commonName)
	{
		this(commonName, null);
	}

	/**
	 * Constructs new IniUtils instance with a common section. Options in the
	 * common section are used as defaults for all other sections.
	 * 
	 * @param commonName name of the common section
	 * @param isCaseSensitive section and option names are case-sensitive if this is true
	 */
	public IniUtils(String commonName, boolean isCaseSensitive)
	{
		this(commonName, null, isCaseSensitive);
	}

	/**
	 * Constructs new IniUtils defining comment delimiters.
	 * 
	 * @param delims an array of characters to be recognized as starters of comment
	 * lines; the first of them will be used for newly created
	 * comments
	 */
	public IniUtils(char[] delims)
	{
		this(null, delims);
	}

	/**
	 * Constructs new IniUtils defining comment delimiters.
	 * 
	 * @param delims an array of characters to be recognized as starters of comment
	 * lines; the first of them will be used for newly created
	 * comments
	 * @param isCaseSensitive section and option names are case-sensitive if this is true
	 */
	public IniUtils(char[] delims, boolean isCaseSensitive)
	{
		this(null, delims, isCaseSensitive);
	}

	/**
	 * Constructs new IniUtils instance with a common section, defining comment
	 * delimiters. Options in the common section are used as defaults for all
	 * other sections.
	 * 
	 * @param commonName name of the common section
	 * @param delims an array of characters to be recognized as starters of comment
	 * lines; the first of them will be used for newly created
	 * comments
	 */
	public IniUtils(String commonName, char[] delims)
	{
		this(commonName, delims, defaultCaseSensitivity);
	}

	/**
	 * Constructs new IniUtils instance with a common section, defining comment
	 * delimiters. Options in the common section are used as defaults for all
	 * other sections.
	 * 
	 * @param commonName name of the common section
	 * @param delims an array of characters to be recognized as starters of comment
	 * lines; the first of them will be used for newly created
	 * comments
	 * @param isCaseSensitive the is case sensitive
	 */
	public IniUtils(String commonName, char[] delims, boolean isCaseSensitive)
	{
		this.sections = new HashMap<String, Section>();
		this.sectionOrder = new LinkedList<String>();
		this.isCaseSensitive = isCaseSensitive;
		if (commonName != null)
		{
			this.commonName = commonName;
			addSection(this.commonName);
		}
		this.commentDelims = delims;
	}

	/**
	 * Returns the value of a given option in a given section or null if either
	 * the section or the option don't exist. If a common section was defined
	 * options are also looked up there if they're not present in the specific
	 * section.
	 * 
	 * @param section the section's name
	 * @param option the option's name
	 * 
	 * @return the option's value
	 * 
	 * @throws NullPointerException any of the arguments is <code>null</code>
	 */
	public String get(String section, String option)
	{
		if (hasSection(section))
		{
			Section sect = getSection(section);
			if (sect.hasOption(option))
			{
				return sect.get(option);
			}
			if (this.commonName != null)
			{
				return getSection(this.commonName).get(option);
			}
		}
		return null;
	}

	/**
	 * Sets the value of an option in a section, if the option exist, otherwise
	 * adds the option to the section. Trims white space from the start and the
	 * end of the value and deletes newline characters it might contain.
	 * 
	 * @param section the section's name
	 * @param option the option's name
	 * @param value the option's value
	 * 
	 * @throws IniUtils.NoSuchSectionException no section with the given name exists
	 * @throws IllegalArgumentException the option name is illegal, ie contains a '=' character or
	 * consists only of white space
	 * @throws NullPointerException section or option are <code>null</code>
	 */
	public void set(String section, String option, String value)
	{
		if (hasSection(section))
		{
			getSection(section).set(option, value);
		}
		else
		{
			throw new NoSuchSectionException(section);
		}
	}

	/**
	 * Removes an option from a section if it exists. Will not remove options
	 * from the common section if it's not directly addressed.
	 * 
	 * @param section the section's name
	 * @param option the option's name
	 * 
	 * @return <code>true</code> if the option was actually removed
	 * 
	 * @throws IniUtils.NoSuchSectionException no section with the given name exists
	 */
	public boolean remove(String section, String option)
	{
		if (hasSection(section))
		{
			return getSection(section).remove(option);
		}
		else
		{
			throw new NoSuchSectionException(section);
		}
	}

	/**
	 * Checks whether an option exists in a given section. Options in the common
	 * section are assumed to not exist in particular sections, unless they're
	 * overwritten.
	 * 
	 * @param section the section's name
	 * @param option the option's name
	 * 
	 * @return true if the given section has the option
	 */
	public boolean hasOption(String section, String option)
	{
		return hasSection(section) && getSection(section).hasOption(option);
	}

	/**
	 * Checks whether a section with a particular name exists in this instance.
	 * 
	 * @param name the name of the section
	 * 
	 * @return true if the section exists
	 */
	public boolean hasSection(String name)
	{
		return this.sections.containsKey(normSection(name));
	}

	/**
	 * Adds a section if it doesn't exist yet.
	 * 
	 * @param name the name of the section
	 * 
	 * @return <code>true</code> if the section didn't already exist
	 * 
	 * @throws IllegalArgumentException the name is illegal, ie contains one of the characters '['
	 * and ']' or consists only of white space
	 */
	public boolean addSection(String name)
	{
		String normName = normSection(name);
		if (!hasSection(normName))
		{
			// Section constructor might throw IllegalArgumentException
			this.sections.put(normName, new Section(normName, this.commentDelims, this.isCaseSensitive));
			this.sectionOrder.add(normName);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Removes a section if it exists.
	 * 
	 * @param name the section's name
	 * 
	 * @return <code>true</code> if the section actually existed
	 * 
	 * @throws IllegalArgumentException when trying to remove the common section
	 */
	public boolean removeSection(String name)
	{
		String normName = normSection(name);
		if (this.commonName != null && this.commonName.equals(normName))
		{
			throw new IllegalArgumentException("Can't remove common section");
		}
		if (hasSection(normName))
		{
			this.sections.remove(normName);
			this.sectionOrder.remove(normName);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns all section names in this instance minus the common section if
	 * one was defined.
	 * 
	 * @return list of the section names in original/insertion order
	 */
	@SuppressWarnings("unchecked")
	public List sectionNames()
	{
		List sectList = new ArrayList(this.sectionOrder);
		if (this.commonName != null)
		{
			sectList.remove(this.commonName);
		}
		return sectList;
	}

	/**
	 * Returns all option names of a section, not including options from the
	 * common section.
	 * 
	 * @param section the section's name
	 * 
	 * @return list of option names
	 * 
	 * @throws IniUtils.NoSuchSectionException no section with the given name exists
	 */
	public List<String> optionNames(String section)
	{
		if (hasSection(section))
		{
			return getSection(section).optionNames();
		}
		else
		{
			throw new NoSuchSectionException(section);
		}
	}

	/**
	 * Adds a comment line to the end of a section. A comment spanning several
	 * lines (ie with line breaks) will be split up, one comment line for each
	 * line.
	 * 
	 * @param section the section's name
	 * @param comment the comment
	 * 
	 * @throws IniUtils.NoSuchSectionException no section with the given name exists
	 */
	public void addComment(String section, String comment)
	{
		if (hasSection(section))
		{
			getSection(section).addComment(comment);
		}
		else
		{
			throw new NoSuchSectionException(section);
		}
	}

	/**
	 * Adds a blank line to the end of a section.
	 * 
	 * @param section the section's name
	 * 
	 * @throws IniUtils.NoSuchSectionException no section with the given name exists
	 */
	public void addBlankLine(String section)
	{
		if (hasSection(section))
		{
			getSection(section).addBlankLine();
		}
		else
		{
			throw new NoSuchSectionException(section);
		}
	}

	/**
	 * Writes this instance in INI format to a file.
	 * 
	 * @param filename the file to write to
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void save(String filename) throws IOException
	{
		save(new File(filename));
	}

	/**
	 * Writes this instance in INI format to a file.
	 * 
	 * @param file where to save to
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void save(File file) throws IOException
	{
		OutputStream out = new FileOutputStream(file);
		save(out);
		out.close();
	}

	/**
	 * Writes this instance in INI format to an output stream. This method takes
	 * an <code>OutputStream</code> for maximum flexibility, internally it
	 * does of course use a writer for character based output.
	 * 
	 * @param stream where to write
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void save(OutputStream stream) throws IOException
	{
		save(new OutputStreamWriter(stream));
	}

	/**
	 * Writes this instance in INI format to an output stream writer.
	 * 
	 * @param streamWriter where to write
	 * 
	 * @throws IOException at an I/O problem
	 */
	@SuppressWarnings("unchecked")
	public void save(OutputStreamWriter streamWriter) throws IOException
	{
		Iterator it = this.sectionOrder.iterator();
		PrintWriter writer = new PrintWriter(streamWriter, true);
		while (it.hasNext())
		{
			Section sect = getSection((String) it.next());
			writer.println(sect.header());
			sect.save(writer);
		}
	}

	/**
	 * Loads INI formatted input from a file into this instance, using the
	 * default character encoding. Everything in the file before the first
	 * section header is ignored.
	 * 
	 * @param filename file to read from
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void load(String filename) throws IOException
	{
		load(new File(filename));
	}

	/**
	 * Loads INI formatted input from a file into this instance, using the
	 * default character encoding. Everything in the file before the first
	 * section header is ignored.
	 * 
	 * @param file file to read from
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void load(File file) throws IOException
	{
		InputStream in = new FileInputStream(file);
		// InputStream in =
		// IniUtils.class.getClassLoader().getResourceAsStream(file.getName());
		load(in);
		in.close();
	}

	/**
	 * Loads INI formatted input from a stream into this instance, using the
	 * default character encoding. This method takes an <code>InputStream</code>
	 * for maximum flexibility, internally it does use a reader (using the
	 * default character encoding) for character based input. Everything in the
	 * stream before the first section header is ignored.
	 * 
	 * @param stream where to read from
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void load(InputStream stream) throws IOException
	{
		load(new InputStreamReader(stream));
	}

	/**
	 * Loads INI formatted input from a stream reader into this instance.
	 * Everything in the stream before the first section header is ignored.
	 * 
	 * @param streamReader where to read from
	 * 
	 * @throws IOException at an I/O problem
	 */
	public void load(InputStreamReader streamReader) throws IOException
	{
		BufferedReader reader = new BufferedReader(streamReader);
		String curSection = null;
		String line = null;

		while (reader.ready())
		{
			line = reader.readLine().trim();
			if (line.length() > 0 && line.charAt(0) == Section.HEADER_START)
			{
				int endIndex = line.indexOf(Section.HEADER_END);
				if (endIndex >= 0)
				{
					curSection = line.substring(1, endIndex);
					addSection(curSection);
				}
			}
			if (curSection != null)
			{
				Section sect = getSection(curSection);
				sect.load(reader);
			}
		}
	}

	/**
	 * Returns a section by name or <code>null</code> if not found.
	 * 
	 * @param name the section's name
	 * 
	 * @return the section
	 */
	private Section getSection(String name)
	{
		return (Section) sections.get(normSection(name));
	}

	/**
	 * Normalizes an arbitrary string for use as a section name. Currently only
	 * makes the string lower-case (provided this instance isn't case-
	 * sensitive) and trims leading and trailing white space. Note that
	 * normalization isn't enforced by the Section class.
	 * 
	 * @param name the string to be used as section name
	 * 
	 * @return a normalized section name
	 */
	private String normSection(String name)
	{
		if (!this.isCaseSensitive)
		{
			name = name.toLowerCase();
		}
		return name.trim();
	}

	/**
	 * To string array.
	 * 
	 * @param coll the coll
	 * 
	 * @return the string[]
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private static String[] toStringArray(Collection coll)
	{
		Object[] objArray = coll.toArray();
		String[] strArray = new String[objArray.length];
		for (int i = 0; i < objArray.length; i++)
		{
			strArray[i] = (String) objArray[i];
		}
		return strArray;
	}

	/**
	 * Loads, edits and saves a section of an INI-style configuration file. This
	 * class does actually belong to the internals of {@link IniUtils} and
	 * should rarely ever be used directly. It's exposed because it can be
	 * useful for plain, section-less configuration files (Java-style
	 * properties, for example).
	 */
	public static class Section
	{

		/** The name. */
		private String name;

		/** The options. */
		private Map<String, Option> options;

		/** The lines. */
		private List<Line> lines;

		/** The option delims. */
		private char[] optionDelims;

		/** The option delims sorted. */
		private char[] optionDelimsSorted;

		/** The comment delims. */
		private char[] commentDelims;

		/** The comment delims sorted. */
		private char[] commentDelimsSorted;

		/** The is case sensitive. */
		private boolean isCaseSensitive;

		/** The Constant DEFAULT_OPTION_DELIMS. */
		private static final char[] DEFAULT_OPTION_DELIMS = new char[] { '=', ':' };

		/** The Constant DEFAULT_COMMENT_DELIMS. */
		private static final char[] DEFAULT_COMMENT_DELIMS = new char[] { '#', ';' };

		/** The Constant OPTION_DELIMS_WHITESPACE. */
		private static final char[] OPTION_DELIMS_WHITESPACE = new char[] { ' ', '\t' };

		/** The Constant DEFAULT_CASE_SENSITIVITY. */
		private static final boolean DEFAULT_CASE_SENSITIVITY = false;

		/** The Constant HEADER_START. */
		public static final char HEADER_START = '[';

		/** The Constant HEADER_END. */
		public static final char HEADER_END = ']';

		/** The Constant NAME_MAXLENGTH. */
		private static final int NAME_MAXLENGTH = 1024;

		/** The Constant INVALID_NAME_CHARS. */
		private static final char[] INVALID_NAME_CHARS = { HEADER_START, HEADER_END };

		/**
		 * Constructs a new section.
		 * 
		 * @param name the section's name
		 * 
		 * @throws IllegalArgumentException the section's name is illegal
		 */
		public Section(String name)
		{
			this(name, null);
		}

		/**
		 * Constructs a new section, specifying case-sensitivity.
		 * 
		 * @param name the section's name
		 * @param isCaseSensitive option names are case-sensitive if this is true
		 * 
		 * @throws IllegalArgumentException the section's name is illegal
		 */
		public Section(String name, boolean isCaseSensitive)
		{
			this(name, null, isCaseSensitive);
		}

		/**
		 * Constructs a new section, defining comment delimiters.
		 * 
		 * @param name the section's name
		 * @param delims an array of characters to be recognized as starters of
		 * comment lines; the first of them will be used for newly
		 * created comments
		 * 
		 * @throws IllegalArgumentException the section's name is illegal
		 */
		public Section(String name, char[] delims)
		{
			this(name, delims, DEFAULT_CASE_SENSITIVITY);
		}

		/**
		 * Constructs a new section, defining comment delimiters.
		 * 
		 * @param name the section's name
		 * @param delims an array of characters to be recognized as starters of
		 * comment lines; the first of them will be used for newly
		 * created comments
		 * @param isCaseSensitive option names are case-sensitive if this is true
		 * 
		 * @throws IllegalArgumentException the section's name is illegal
		 */
		public Section(String name, char[] delims, boolean isCaseSensitive)
		{
			if (!validName(name))
			{
				throw new IllegalArgumentException("Illegal section name:" + name);
			}
			this.name = name;
			this.isCaseSensitive = isCaseSensitive;
			this.options = new HashMap<String, Option>();
			this.lines = new LinkedList<Line>();
			this.optionDelims = DEFAULT_OPTION_DELIMS;
			this.commentDelims = (delims == null ? DEFAULT_COMMENT_DELIMS : delims);
			// sorting so we can later use binary search
			this.optionDelimsSorted = new char[this.optionDelims.length];
			System.arraycopy(this.optionDelims, 0, this.optionDelimsSorted, 0, this.optionDelims.length);
			this.commentDelimsSorted = new char[this.commentDelims.length];
			System.arraycopy(this.commentDelims, 0, this.commentDelimsSorted, 0, this.commentDelims.length);
			Arrays.sort(this.optionDelimsSorted);
			Arrays.sort(this.commentDelimsSorted);
		}

		/**
		 * Returns the names of all options in this section.
		 * 
		 * @return list of names of this section's options in original/insertion
		 * order
		 */
		@SuppressWarnings("unchecked")
		public List<String> optionNames()
		{
			List<String> optNames = new LinkedList<String>();
			Iterator it = this.lines.iterator();
			while (it.hasNext())
			{
				Object line = it.next();
				if (line instanceof Option)
				{
					optNames.add(((Option) line).name());
				}
			}
			return optNames;
		}

		/**
		 * Checks whether a given option exists in this section.
		 * 
		 * @param name the name of the option to test for
		 * 
		 * @return true if the option exists in this section
		 */
		public boolean hasOption(String name)
		{
			return this.options.containsKey(normOption(name));
		}

		/**
		 * Returns an option's value.
		 * 
		 * @param option the name of the option
		 * 
		 * @return the requested option's value or <code>null</code> if no
		 * option with the specified name exists
		 */
		public String get(String option)
		{
			String normed = normOption(option);
			if (hasOption(normed))
			{
				return getOption(normed).value();
			}
			return null;
		}

		/**
		 * Sets an option's value and creates the option if it doesn't exist.
		 * 
		 * @param option the option's name
		 * @param value the option's value
		 * 
		 * @throws IllegalArgumentException the option name is illegal, ie contains a '=' character
		 * or consists only of white space
		 */
		public void set(String option, String value)
		{
			set(option, value, this.optionDelims[0]);
		}

		/**
		 * Sets an option's value and creates the option if it doesn't exist.
		 * 
		 * @param option the option's name
		 * @param value the option's value
		 * @param delim the delimiter between name and value for this option
		 * 
		 * @throws IllegalArgumentException the option name is illegal, ie contains a '=' character
		 * or consists only of white space
		 */
		public void set(String option, String value, char delim)
		{
			option = option.trim();
			value = value.trim();
			String normed = normOption(option);
			if (hasOption(normed))
			{
				getOption(normed).set(value);
			}
			else
			{
				// Option constructor might throw IllegalArgumentException
				Option opt = new Option(normed, value, delim);
				this.options.put(normed, opt);
				this.lines.add(opt);
			}
		}

		/**
		 * Removes an option if it exists.
		 * 
		 * @param option the name of the option
		 * 
		 * @return <code>true</code> if the option was actually removed
		 */
		public boolean remove(String option)
		{
			String normed = normOption(option);
			if (hasOption(normed))
			{
				this.lines.remove(getOption(normed));
				this.options.remove(normed);
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * Adds a comment line to the end of this section. A comment spanning
		 * several lines (ie with line breaks) will be split up, one comment
		 * line for each line.
		 * 
		 * @param comment the comment
		 */
		public void addComment(String comment)
		{
			addComment(comment, this.commentDelims[0]);
		}

		/**
		 * Adds a comment line to the end of this section. A comment spanning
		 * several lines (ie with line breaks) will be split up, one comment
		 * line for each line.
		 * 
		 * @param comment the comment
		 * @param delim the delimiter used to mark the start of this comment
		 */
		public void addComment(String comment, char delim)
		{
			StringTokenizer st = new StringTokenizer(comment.trim(), NEWLINE_CHARS);
			while (st.hasMoreTokens())
			{
				this.lines.add(new Comment(st.nextToken(), delim));
			}
		}

		/** The Constant NEWLINE_CHARS. */
		private static final String NEWLINE_CHARS = "\n\r";

		/**
		 * Adds a blank line to the end of this section.
		 */
		public void addBlankLine()
		{
			this.lines.add(BLANK_LINE);
		}

		/**
		 * Loads options from a reader into this instance. Will read from the
		 * stream until it hits a section header, ie a '[' character, and resets
		 * the reader to point to this character.
		 * 
		 * @param reader where to read from
		 * 
		 * @throws IOException at an I/O problem
		 */
		public void load(BufferedReader reader) throws IOException
		{
			while (reader.ready())
			{
				reader.mark(NAME_MAXLENGTH);
				String line = reader.readLine().trim();
				// Check for section header
				if (line.length() > 0 && line.charAt(0) == HEADER_START)
				{
					reader.reset();
					return;
				}

				int delimIndex = -1;
				// blank line
				if (line.equals(""))
				{
					this.addBlankLine();
				}
				// comment line
				else if ((delimIndex = Arrays.binarySearch(this.commentDelimsSorted, line.charAt(0))) >= 0)
				{
					addComment(line.substring(1), this.commentDelimsSorted[delimIndex]);
				}
				// option line
				else
				{
					delimIndex = -1;
					int delimNum = -1;
					int lastSpaceIndex = -1;

					// Customized for BMG. BMG uses only '=' as delimiter,
					// The following 'if' loop speeds up loading
					if (line.contains("="))
					{
						delimIndex = line.indexOf("=");
						this
								.set(line.substring(0, delimIndex), line.substring(delimIndex + 1), line
										.charAt(delimIndex));
						continue;
					}

					// Customized for BMG- till here
					for (int i = 0, l = line.length(); i < l && delimIndex < 0; i++)
					{

						delimNum = Arrays.binarySearch(this.optionDelimsSorted, line.charAt(i));
						if (delimNum >= 0)
						{
							delimIndex = i;
						}
						else
						{
							boolean isSpace = Arrays.binarySearch(OPTION_DELIMS_WHITESPACE, line.charAt(i)) >= 0;

							if (!isSpace && lastSpaceIndex >= 0)
							{
								break;
							}
							else if (isSpace)
							{
								lastSpaceIndex = i;
							}
						}
					}
					// delimiter at start of line
					if (delimIndex == 0)
					{
						// what's a man got to do?
					}
					// no delimiter found
					else if (delimIndex < 0)
					{
						if (lastSpaceIndex < 0)
						{
							this.set(line, "");
						}
						else
						{
							this.set(line.substring(0, lastSpaceIndex), line.substring(lastSpaceIndex + 1));
						}
					}
					// delimiter found
					else
					{
						this
								.set(line.substring(0, delimIndex), line.substring(delimIndex + 1), line
										.charAt(delimIndex));
					}
				}
			}
		}

		/**
		 * Prints this section to a print writer.
		 * 
		 * @param writer where to write
		 * 
		 * @throws IOException at an I/O problem
		 */
		@SuppressWarnings("unchecked")
		public void save(PrintWriter writer) throws IOException
		{
			Iterator it = this.lines.iterator();
			while (it.hasNext())
			{
				writer.println(((Line) it.next()).toString());
			}
			if (writer.checkError())
			{
				throw new IOException();
			}
		}

		/**
		 * Returns an actual Option instance.
		 * 
		 * @param name the name
		 * 
		 * @return the requested Option instance
		 * 
		 * @throws NullPointerException if no option with the specified name exists
		 */
		private Option getOption(String name)
		{
			return (Option) this.options.get(name);
		}

		/**
		 * Returns the bracketed header of this section as appearing in an
		 * actual INI file.
		 * 
		 * @return the section's name in brackets
		 */
		private String header()
		{
			return HEADER_START + this.name + HEADER_END;
		}

		/**
		 * Checks a string for validity as a section name. It can't contain the
		 * characters '[' and ']'. An empty string or one consisting only of
		 * white space isn't allowed either.
		 * 
		 * @param name the name to validate
		 * 
		 * @return true if the name validates as a section name
		 */
		private static boolean validName(String name)
		{
			if (name.trim().equals(""))
			{
				return false;
			}
			for (int i = 0; i < INVALID_NAME_CHARS.length; i++)
			{
				if (name.indexOf(INVALID_NAME_CHARS[i]) >= 0)
				{
					return false;
				}
			}
			return true;
		}

		/**
		 * Normalizes an arbitrary string for use as an option name, ie makes it
		 * lower-case (provided this section isn't case-sensitive) and trims
		 * leading and trailing white space.
		 * 
		 * @param name the string to be used as option name
		 * 
		 * @return a normalized option name
		 */
		private String normOption(String name)
		{
			if (!this.isCaseSensitive)
			{
				name = name.toLowerCase();
			}
			return name.trim();
		}

	}

	/**
	 * The Interface Line.
	 */
	private interface Line
	{

		/**
		 * To string.
		 * 
		 * @return the string
		 */
		public String toString();
	}

	/** The Constant BLANK_LINE. */
	private static final Line BLANK_LINE = new Line()
	{
		public String toString()
		{
			return "";
		}
	};

	/**
	 * The Class Option.
	 */
	private static class Option implements Line
	{

		/** The name. */
		private String name;

		/** The value. */
		private String value;

		/** The separator. */
		private char separator;

		/** The Constant DEFAULT_SEPARATOR. */
		private static final char DEFAULT_SEPARATOR = '=';

		/** The Constant ILLEGAL_VALUE_CHARS. */
		private static final String ILLEGAL_VALUE_CHARS = "\n\r";

		/**
		 * Instantiates a new option.
		 * 
		 * @param name the name
		 * @param value the value
		 */
		public Option(String name, String value)
		{
			this(name, value, DEFAULT_SEPARATOR);
		}

		/**
		 * Instantiates a new option.
		 * 
		 * @param name the name
		 * @param value the value
		 * @param separator the separator
		 */
		public Option(String name, String value, char separator)
		{
			if (!validName(name, separator))
			{
				throw new IllegalArgumentException("Illegal option name:" + name);
			}
			this.name = name;
			this.separator = separator;
			set(value);
		}

		/**
		 * Name.
		 * 
		 * @return the string
		 */
		public String name()
		{
			return this.name;
		}

		/**
		 * Value.
		 * 
		 * @return the string
		 */
		public String value()
		{
			return this.value;
		}

		/**
		 * Sets the.
		 * 
		 * @param value the value
		 */
		public void set(String value)
		{
			if (value == null)
			{
				this.value = value;
			}
			else
			{
				StringTokenizer st = new StringTokenizer(value.trim(), ILLEGAL_VALUE_CHARS);
				StringBuffer sb = new StringBuffer();
				// this might not be particularly efficient
				while (st.hasMoreTokens())
				{
					sb.append(st.nextToken());
				}
				this.value = sb.toString();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return this.name + ' ' + this.separator + ' ' + this.value;
		}

		/**
		 * Valid name.
		 * 
		 * @param name the name
		 * @param separator the separator
		 * 
		 * @return true, if successful
		 */
		private static boolean validName(String name, char separator)
		{
			if (name.trim().equals(""))
			{
				return false;
			}
			if (name.indexOf(separator) >= 0)
			{
				return false;
			}
			return true;
		}

	}

	/**
	 * The Class Comment.
	 */
	private static class Comment implements Line
	{

		/** The comment. */
		private String comment;

		/** The delimiter. */
		private char delimiter;

		/** The Constant DEFAULT_DELIMITER. */
		private static final char DEFAULT_DELIMITER = '#';

		/**
		 * Instantiates a new comment.
		 * 
		 * @param comment the comment
		 */
		public Comment(String comment)
		{
			this(comment, DEFAULT_DELIMITER);
		}

		/**
		 * Instantiates a new comment.
		 * 
		 * @param comment the comment
		 * @param delimiter the delimiter
		 */
		public Comment(String comment, char delimiter)
		{
			this.comment = comment.trim();
			this.delimiter = delimiter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return this.delimiter + " " + this.comment;
		}

	}

	/**
	 * Thrown when an inexistent section is addressed.
	 */
	public static class NoSuchSectionException extends RuntimeException
	{

		/** * */
	    private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new no such section exception.
		 */
		public NoSuchSectionException()
		{
			super();
		}

		/**
		 * Instantiates a new no such section exception.
		 * 
		 * @param msg the msg
		 */
		public NoSuchSectionException(String msg)
		{
			super(msg);
		}
	}

}
