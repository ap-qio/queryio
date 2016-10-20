package com.queryio.sysmoncommon.sysmon.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import com.jcraft.jsch.Channel;

public class VT100Emulator 
{
	/** This is a character processing state: Initial state. */
	private static final int ANSISTATE_INITIAL = 0;

	/** This is a character processing state: We've seen an escape character. */
	private static final int ANSISTATE_ESCAPE = 1;

	/**
	 * This is a character processing state: We've seen a '[' after an escape
	 * character. Expecting a parameter character or a command character next.
	 */
	private static final int ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND = 2;

	/**
	 * This is a character processing state: We've seen a ']' after an escape
	 * character. We are now expecting an operating system command that
	 * reprograms an intelligent terminal.
	 */
	private static final int ANSISTATE_EXPECTING_OS_COMMAND = 3;

	/**
	 * This field holds the current state of the Finite TerminalState Automaton (FSA)
	 * that recognizes ANSI escape sequences.
	 *
	 * @see #processNewText()
	 */
	private int ansiState = ANSISTATE_INITIAL;

	/**
	 * This field holds an array of StringBuffer objects, each of which is one
	 * parameter from the current ANSI escape sequence. For example, when
	 * parsing the escape sequence "\e[20;10H", this array holds the strings
	 * "20" and "10".
	 */
	private final StringBuffer[] ansiParameters = new StringBuffer[16];

	/**
	 * This field holds the OS-specific command found in an escape sequence of
	 * the form "\e]...\u0007".
	 */
	private final StringBuffer ansiOsCommand = new StringBuffer(128);

	/**
	 * This field holds the index of the next unused element of the array stored
	 * in field {@link #ansiParameters}.
	 */
	private int nextAnsiParameter = 0;

	Reader fReader;
	
	OutputStream ostream;
	
	public VT100Emulator(Reader reader, OutputStream os) 
	{
		for (int i = 0; i < ansiParameters.length; ++i) 
		{
			ansiParameters[i] = new StringBuffer();
		}
		fReader = reader;
		ostream = os;
	}
	
	public void processText(Channel channel) throws Exception
	{
		while (true)
		{
			processNewText();
			if (channel.isClosed() || channel.isEOF())
			{
				break;
			}
			else if (!fReader.ready())
			{
				Thread.sleep(500);
			}
		}
	}

	private void processNewText() throws IOException 
	{
		// Scan the newly received text.
		while (hasNextChar()) 
		{
			char character = getNextChar();
			switch (ansiState) 
			{
			case ANSISTATE_INITIAL:
				switch (character) 
				{
				case '\u0000':
					break; // NUL character. Ignore it.

				case '\u0007':
					//processBEL(); // BEL (Control-G)
					break;

				case '\b':
					//processBackspace(); // Backspace
					break;

				case '\t':
					//System.out.print(String.valueOf(character));
					appendText(String.valueOf(character));
					//processTab(); // Tab.
					break;

				case '\n':
					//System.out.print(String.valueOf(character));
					appendText(String.valueOf(character));
					/*
					processNewline(); // Newline (Control-J)
					if (fCrAfterNewLine)
					{
						processCarriageReturn(); // Carriage Return  (Control-M)
					}
					*/
					break;

				case '\r':
					appendText(String.valueOf(character));
//					System.out.println(output.toString());
//					output.setLength(0);
					//processCarriageReturn(); // Carriage Return (Control-M)
					break;

				case '\u001b':
					ansiState = ANSISTATE_ESCAPE; // Escape.
					break;

				default:
					processNonControlCharacters(character);
					break;
				}
				break;

			case ANSISTATE_ESCAPE:
				// We've seen an escape character. Here, we process the
				// character
				// immediately following the escape.

				switch (character) {
				case '[':
					ansiState = ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND;
					nextAnsiParameter = 0;

					// Erase the parameter strings in preparation for optional
					// parameter characters.

					for (int i = 0; i < ansiParameters.length; ++i) {
						ansiParameters[i].delete(0, ansiParameters[i].length());
					}
					break;

				case ']':
					ansiState = ANSISTATE_EXPECTING_OS_COMMAND;
					ansiOsCommand.delete(0, ansiOsCommand.length());
					break;

				case '7':
					// Save cursor position and character attributes

					ansiState = ANSISTATE_INITIAL;
					break;

				case '8':
					// Restore cursor and attributes to previously saved
					// position
					ansiState = ANSISTATE_INITIAL;
					break;

				case 'c':
					// Reset the terminal
					ansiState = ANSISTATE_INITIAL;
					//resetTerminal();
					break;

				default:
					//System.out.println("Unsupported escape sequence: escape '" + character + "'"); //$NON-NLS-1$ //$NON-NLS-2$
					ansiState = ANSISTATE_INITIAL;
					break;
				}
				break;

			case ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND:
				// Parameters can appear after the '[' in an escape sequence,
				// but they
				// are optional.

				if (character == '@' || (character >= 'A' && character <= 'Z')
						|| (character >= 'a' && character <= 'z')) 
				{
					ansiState = ANSISTATE_INITIAL;
					//processAnsiCommandCharacter(character);
				}
				else 
				{
					processAnsiParameterCharacter(character);
				}
				break;

			case ANSISTATE_EXPECTING_OS_COMMAND:
				// A BEL (\u0007) character marks the end of the OSC sequence.
				if (character == '\u0007') 
				{
					ansiState = ANSISTATE_INITIAL;
					processAnsiOsCommand();
				}
				else 
				{
					ansiOsCommand.append(character);
				}
				break;

			default:
				// This should never happen! If it does happen, it means there
				// is a
				// bug in the FSA. For robustness, we return to the initial
				// state.
				//System.out.println("INVALID ANSI FSA STATE: " + ansiState); //$NON-NLS-1$
				ansiState = ANSISTATE_INITIAL;
				break;
			}
		}
	}
	
	private void processAnsiOsCommand() 
	{
		if (ansiOsCommand.charAt(0) != '0' || ansiOsCommand.charAt(1) != ';') 
		{
			return;
		}
	}
	
	
	private void processAnsiParameterCharacter(char ch) 
	{
		if (ch == ';') 
		{
			++nextAnsiParameter;
		}
		else 
		{
			if (nextAnsiParameter < ansiParameters.length)
				ansiParameters[nextAnsiParameter].append(ch);
		}
	}
	
	
	private int fNextChar=-1;
	private char getNextChar() throws IOException 
	{
		int c=-1;
		if(fNextChar!=-1) 
		{
			c= fNextChar;
			fNextChar=-1;
		}
		else 
		{
			c = fReader.read();
		}
		// TO DO: better end of file handling
		if(c==-1) c = 0;
		return (char)c;
	}

	private boolean hasNextChar() throws IOException  {
		if(fNextChar>=0)
			return true;
		return fReader.ready();
	}

	/**
	 * Put back one character to the stream. This method can push
	 * back exactly one character. The character is the next character
	 * returned by {@link #getNextChar}
	 * @param c the character to be pushed back.
	 */
	void pushBackChar(char c) 
	{
		//assert fNextChar!=-1: "Already a character waiting:"+fNextChar; //$NON-NLS-1$
		fNextChar = c;
	}
	
	private void processNonControlCharacters(char character) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(character);
		// Identify a contiguous sequence of non-control characters, starting at
		// firstNonControlCharacterIndex in newText.
		while(hasNextChar()) {
			character=getNextChar();
			if(character == '\u0000' || character == '\b' || character == '\t'
				|| character == '\u0007' || character == '\n'
				|| character == '\r' || character == '\u001b') {
				pushBackChar(character);
				break;
			}
			buffer.append(character);
		}
		// Now insert the sequence of non-control characters in the StyledText widget
		// at the location of the cursor.
		appendText(buffer.toString());
	}

	private void appendText(String string) throws IOException 
	{
//		System.out.print(string);
		ostream.write(string.getBytes());
		ostream.flush();
	}
	
}
