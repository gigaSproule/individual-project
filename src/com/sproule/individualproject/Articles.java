package com.sproule.individualproject;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Articles extends Activity {
	private String articleFile;
	private final int SCROLL_VIEW = 0;
	private final int ARTICLE_TITLE = 1;
	private final int ARTICLE_INTROS = 2;
	private final int SECTION_BUTTONS = 100;
	private final int SECTIONS = 200;
	private final int SECTIONS_TITLE_LAYOUT = 300;
	private final int SECTION_CATEGORIES = 400;
	private final int SECTION_TITLES = 500;
	private final int SECTION_HEADINGS = 600;
	private final int SECTION_CATEGORY_PARAGRAPHS = 700;
	private final int SECTION_CATEGORY_ITEMS = 800;
	private final int SECTION_TABLES = 900;
	private final int SECTION_TABLE_TITLES = 1000;
	private final int SECTION_TABLE_HEADERS = 1100;
	private final int SECTION_TABLE_CONTENTS = 1200;
	private final int SECTION_IMAGE_TEXTS = 1300;
	private final float DEFAULT_ARTICLE_TITLE_SIZE = 33.0f;
	private final float DEFAULT_HEADING_SIZE = 27.0f;
	private final float DEFAULT_TEXT_SIZE = 21.0f;
	private final float DEFAULT_CHANGE_TEXT_SIZE_BY = 4.0f;
	private final float MINIMUM_TEXT_SIZE = 14.0f;
	private final float MAXIMUM_TEXT_SIZE = 52.0f;
	private int numberOfIntroParagraphs = 0;
	private int numberOfTables = 0;
	private int numberOfTableTitles = 0;
	private int numberOfTableHeaders = 0;
	private int numberOfTableContents = 0;
	private int numberOfTitles = 0;
	private int numberOfHeadings = 0;
	private int numberOfCategoryParagraphs = 0;
	private int numberOfCategoryItems = 0;
	private int numberOfImages = 0;
	private int articleTextIndex = 0;
	private int numberOfSections = 0;
	private int categoryIndex = 0;
	private long timeStarted = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		articleFile = extras.getString("articleFile");

		ScrollView scrollView = new ScrollView(this);
		scrollView.setBackgroundColor(Color.WHITE);
		scrollView.setId(SCROLL_VIEW);

		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(Color.TRANSPARENT);

		LinearLayout articleIntroLayout = new LinearLayout(this);
		articleIntroLayout.setBackgroundColor(Color.TRANSPARENT);
		articleIntroLayout.setOrientation(LinearLayout.VERTICAL);

		scrollView.addView(mainLayout);
		mainLayout.addView(articleIntroLayout);

		ArrayList<Article> articleText = new ArrayList<Article>();
		try {
			articleText = ArticleXMLParser.getArticleFromXML(articleFile, this);
		} catch (XmlPullParserException e) {
			Log.e("Articles.onCreate", e.getMessage());
		} catch (IOException e) {
			Log.e("Articles.onCreate", e.getMessage());
		}

		while (articleTextIndex < articleText.size()) {
			if ((articleText.get(articleTextIndex)) instanceof Section) {
				addSectionToMainLayout(mainLayout, articleText);
			} else if ((articleText.get(articleTextIndex)) instanceof Article) {
				addTitleIntroToIntroLayout(articleIntroLayout, articleText);
			}
		}

		setContentView(scrollView);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		String colourMode = preferences.getString("colourMode", "day");

		if (colourMode=="night") {
			setNightMode();
		} else if(colourMode=="sepia") {
			setSepiaMode();
		}
		
		int changeTextSizeBy = preferences.getInt("changeTextSizeBy", 0);
		if (changeTextSizeBy > 0) {
			increaseTextSize(changeTextSizeBy);
		} else {
			decreaseTextSize(changeTextSizeBy);
		}
		
		timeStarted = System.currentTimeMillis();
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		long timeElapsed = preferences.getLong("timeElapsedInSeconds", 0);
		timeElapsed += ((System.currentTimeMillis() - timeStarted) / 1000);
		preferences.edit().putLong("timeElapsedInSeconds", timeElapsed).commit();
	}

	public void addSectionToMainLayout(LinearLayout mainLayout,
			ArrayList<Article> articleText) {
		Section section = (Section) articleText.get(articleTextIndex);

		LinearLayout sectionTitleLayout = new LinearLayout(this);
		sectionTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
		sectionTitleLayout.setBackgroundColor(Color.TRANSPARENT);
		sectionTitleLayout.setId(SECTIONS_TITLE_LAYOUT + numberOfSections);
		mainLayout.addView(sectionTitleLayout);

		Button sectionButton = new Button(this);
		sectionButton.setText("Show");
		sectionButton.setId(SECTION_BUTTONS + numberOfSections);
		sectionTitleLayout.addView(sectionButton);
		
		TextView lblTitle = new TextView(this);
		lblTitle.setBackgroundColor(Color.TRANSPARENT);
		lblTitle.setTextAppearance(this,
				android.R.style.TextAppearance_Medium);
		lblTitle.setId(SECTION_TITLES + numberOfSections);
		lblTitle.setText(section.getTitle());
		sectionTitleLayout.addView(lblTitle);
		numberOfTitles++;

		LinearLayout sectionLayout = new LinearLayout(this);
		sectionLayout.setBackgroundColor(Color.TRANSPARENT);
		sectionLayout.setOrientation(LinearLayout.VERTICAL);
		sectionLayout.setVisibility(View.GONE);
		sectionLayout.setId(SECTIONS + numberOfSections);
		mainLayout.addView(sectionLayout);

		sectionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int id = v.getId() - SECTION_BUTTONS;
				showHideSection((Button) findViewById(SECTION_BUTTONS + id), (LinearLayout) findViewById(SECTIONS + id));
			}
		});

		for (int sectionObjectIndex = 0; sectionObjectIndex < section.getNumberOfObjectsInSection(); sectionObjectIndex++) {
			if ((section.getObjectInSection(sectionObjectIndex)) instanceof Category) {
				addCategoryToSectionLayout(section, sectionLayout, sectionObjectIndex);
			} else if ((section.getObjectInSection(sectionObjectIndex)) instanceof Table) {
				addTableToSectionLayout(section, sectionLayout, sectionObjectIndex);
			}
		}
		articleTextIndex++;
		numberOfSections++;
	}

	public void addCategoryToSectionLayout(Section section,
			LinearLayout sectionLayout, int sectionObjectIndex) {
		Category category = (Category) section.getObjectInSection(sectionObjectIndex);
		int numberOfObjectsInCategory = category.getNumberOfObjects();

		LinearLayout categoryLayout = new LinearLayout(this);
		categoryLayout.setOrientation(LinearLayout.VERTICAL);
		categoryLayout.setId(SECTION_CATEGORIES + categoryIndex);
		categoryIndex++;
		sectionLayout.addView(categoryLayout);
		
		String heading = category.getHeading();
		if (!heading.equals("Unknown")) {
			LayoutParams layoutParams = new LayoutParams();
			layoutParams.setMargins(0, 12, 0, 0);
			
			TextView lblHeading = new TextView(this);
			lblHeading.setBackgroundColor(Color.TRANSPARENT);
			lblHeading.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			lblHeading.setId(SECTION_HEADINGS + numberOfHeadings);
			lblHeading.setLayoutParams(layoutParams);
			lblHeading.setText(heading);
			categoryLayout.addView(lblHeading);
			numberOfHeadings++;
		}
		for (int categoryObjectIndex = 0; categoryObjectIndex < numberOfObjectsInCategory; categoryObjectIndex++) {
			if (category.getObjectInCategory(categoryObjectIndex) instanceof Paragraph) {
				addParagraphToCategoryLayout(category, categoryLayout,
						categoryObjectIndex);
			} else if (category.getObjectInCategory(categoryObjectIndex) instanceof List) {
				addListToCategoryLayout(category, categoryLayout,
						categoryObjectIndex);
			} else if ((category.getObjectInCategory(categoryObjectIndex)) instanceof Image) {
				addImageToCategoryLayout(category, categoryLayout, categoryObjectIndex);
			}
		}
		sectionObjectIndex++;
	}

	public void addListToCategoryLayout(Category category,
			LinearLayout categoryLayout, int categoryObjectIndex) {
		List list = (List) category.getObjectInCategory(categoryObjectIndex);
		
		LinearLayout listLayout = new LinearLayout(this);
		listLayout.setOrientation(LinearLayout.VERTICAL);
		listLayout.setBackgroundColor(Color.TRANSPARENT);
		categoryLayout.addView(listLayout);
		
		for (int xx = 0; xx < list.getNumberOfItems(); xx++) {
			if (list.getListObject(xx) instanceof Item) {
				Item item = (Item) list.getListObject(xx);
				
				LayoutParams params = new LayoutParams();
				params.setMargins(15, 0, 0, 0);
				
				TextView lblListItem = new TextView(this);
				lblListItem.setId(SECTION_CATEGORY_ITEMS + numberOfCategoryItems);
				lblListItem.setLayoutParams(params);
				lblListItem.setText("\u2022 " + item.getItem());
				listLayout.addView(lblListItem);
			} else if (list.getListObject(xx) instanceof SubItem) {
				SubItem subItem = (SubItem) list.getListObject(xx);
				
				LayoutParams params = new LayoutParams();
				params.setMargins(30, 0, 0, 0);
				
				TextView lblListSubItem = new TextView(this);
				lblListSubItem.setId(SECTION_CATEGORY_ITEMS + numberOfCategoryItems);
				lblListSubItem.setLayoutParams(params);
				lblListSubItem.setText("\u25E6 " + subItem.getSubItem());
				listLayout.addView(lblListSubItem);
			}
			numberOfCategoryItems++;
		}
	}

	public void addParagraphToCategoryLayout(Category category,
			LinearLayout categoryLayout, int paragraphIndex) {
		Paragraph paragraph = (Paragraph) category.getObjectInCategory(paragraphIndex);
		TextView lblSParagraph = new TextView(this);
		lblSParagraph.setId(SECTION_CATEGORY_PARAGRAPHS + numberOfCategoryParagraphs);
		lblSParagraph.setText(paragraph.getParagraphText());
		categoryLayout.addView(lblSParagraph);
		numberOfCategoryParagraphs++;
	}

	public void addTableToSectionLayout(Section section,
			LinearLayout sectionLayout, int sectionObjectIndex) {
		int tableHeadersIndex = 0;
		int tableContentsIndex = 0;
		
		Table table = (Table) section.getObjectInSection(sectionObjectIndex);
		int numberOfHeaders = table.getNumberOfHeaders();
		int numberOfRows = table.getNumberOfRows();
		
		if (table.getTableTitle().equals("Unkown")) {
			LayoutParams layoutParams = new LayoutParams();
			layoutParams.setMargins(0, 12, 0, 12);
			
			TextView tableTitle = new TextView(this);
			tableTitle.setBackgroundColor(Color.TRANSPARENT);
			tableTitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			tableTitle.setLayoutParams(layoutParams);
			tableTitle.setId(SECTION_TABLE_TITLES + numberOfTableTitles);
			tableTitle.setText(table.getTableTitle());
			sectionLayout.addView(tableTitle);
			
			numberOfTableTitles++;
		}
		
		HorizontalScrollView tableScrollView = new HorizontalScrollView(this);
		tableScrollView.setBackgroundColor(Color.TRANSPARENT);
		sectionLayout.addView(tableScrollView);
		
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setStretchAllColumns(true);
		tableLayout.setBackgroundColor(Color.BLACK);
		tableLayout.setId(SECTION_TABLES + numberOfTables);
		tableScrollView.addView(tableLayout);
		numberOfTables++;

		for (int aa = 0; aa < numberOfRows; aa++) {
			TableRow tableRow = new TableRow(this);
			tableRow.setBackgroundColor(Color.TRANSPARENT);
			TableRow.LayoutParams cellParams = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			cellParams.setMargins(2, 2, 2, 2);
			tableLayout.addView(tableRow);

			if (aa == 0) {
				for (int ii = 0; ii < numberOfHeaders; ii++) {
					TextView lblTHeader = new TextView(this);
					lblTHeader.setLayoutParams(cellParams);
					lblTHeader.setPadding(2, 0, 2, 0);
					lblTHeader.setBackgroundColor(Color.LTGRAY);
					lblTHeader.setTextAppearance(this, android.R.style.TextAppearance_Medium);
					lblTHeader.setId(SECTION_TABLE_HEADERS + numberOfTableHeaders);
					lblTHeader.setText(table.getHeader(tableHeadersIndex));
					tableRow.addView(lblTHeader);
					tableHeadersIndex++;
					numberOfTableHeaders++;
				}
			} else {
				for (int bb = 0; bb < numberOfHeaders; bb++) {
					TextView lblTContent = new TextView(this);
					lblTContent.setBackgroundColor(Color.LTGRAY);
					lblTContent.setLayoutParams(cellParams);
					lblTContent.setPadding(2, 0, 2, 0);
					lblTContent.setId(SECTION_TABLE_CONTENTS + numberOfTableContents);
					lblTContent.setText(table.getContent(tableContentsIndex));
					tableRow.addView(lblTContent);
					tableContentsIndex++;
					numberOfTableContents++;
				}
			}
		}
		sectionObjectIndex++;
	}
	
	public void addImageToCategoryLayout(Category category, LinearLayout categoryLayout, int categoryObjectIndex) {
		Image image = (Image) category.getObjectInCategory(categoryObjectIndex);
		
		ImageView imageView = new ImageView(this);
		categoryLayout.addView(imageView);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		
		Bitmap bm = BitmapFactory.decodeFile(ArticleManager.PATH + image.getURI(), options);
		imageView.setImageBitmap(bm);
		
		if (!image.getImageText().equals("Unknown")) {
			LayoutParams layoutParams = new LayoutParams();
			layoutParams.setMargins(0, 6, 0, 6);
			
			TextView imageText = new TextView(this);
			imageText.setBackgroundColor(Color.TRANSPARENT);
			imageText.setGravity(Gravity.CENTER);
			imageText.setLayoutParams(layoutParams);
			imageText.setId(SECTION_IMAGE_TEXTS + numberOfImages);
			imageText.setText(image.getImageText());
			categoryLayout.addView(imageText);
			
			numberOfImages++;
		}
	}

	public void addTitleIntroToIntroLayout(LinearLayout articleIntroLayout,
			ArrayList<Article> articleText) {
		Article article = (Article) articleText.get(articleTextIndex);

		TextView lblArticleTitle = new TextView(this);
		lblArticleTitle.setBackgroundColor(Color.TRANSPARENT);
		lblArticleTitle.setTextAppearance(this, R.style.NormalArticleTitleSize);
		lblArticleTitle.setGravity(Gravity.CENTER);
		lblArticleTitle.setId(ARTICLE_TITLE);
		lblArticleTitle.setText(article.getArticleTitle());
		articleIntroLayout.addView(lblArticleTitle);

		numberOfIntroParagraphs = article.getNumberOfIntroParagraphs();
		for (int gg = 0; gg < numberOfIntroParagraphs; gg++) {
			TextView lblIntro = new TextView(this);
			lblIntro.setBackgroundColor(Color.TRANSPARENT);
			lblIntro.setId(ARTICLE_INTROS + gg);
			lblIntro.setText(article.getIntro(gg));
			articleIntroLayout.addView(lblIntro);
		}
		articleTextIndex++;
	}

	public void showHideSection(Button sectionButton, LinearLayout section) {
		boolean visible = section.isShown();
		if (visible) {
			section.setVisibility(View.GONE);
			sectionButton.setText("Show");
		} else {
			section.setVisibility(View.VISIBLE);
			sectionButton.setText("Hide");
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		int changeTextSizeBy = preferences.getInt("changeTextSizeBy", 0);
		SharedPreferences.Editor editor = preferences.edit();
		
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.day:
			setDayMode();
			return true;
		case R.id.night:
			setNightMode();
			return true;
		case R.id.sepia:
			setSepiaMode();
			return true;
		case R.id.increaseText:
			increaseTextSize(1);
			
			changeTextSizeBy++;
			editor.putInt("changeTextSizeBy", changeTextSizeBy);
			editor.commit();
			
			return true;
		case R.id.decreaseText:
			decreaseTextSize(1);

			changeTextSizeBy--;
			editor.putInt("changeTextSizeBy", changeTextSizeBy);
			editor.commit();
			
			return true;
		case R.id.resetTextSize:
			setDefaultTextSize();
			
			editor.putInt("changeTextSizeBy", 0);
			editor.commit();
			
			return true;
		case R.id.search:
			onSearchRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setDayMode() {
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("colourMode", "day");
		editor.commit();

		ScrollView scrollView = (ScrollView) findViewById(SCROLL_VIEW);
		scrollView.setBackgroundColor(Color.WHITE);
		
		((TextView) findViewById(ARTICLE_TITLE)).setTextColor(Color.BLACK);

		for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
			((TextView) findViewById(ARTICLE_INTROS + qq)).setTextColor(Color.BLACK);
		}
		
		for (int zz = 0; zz < numberOfTitles; zz++) {
			((TextView) findViewById(SECTION_TITLES + zz)).setTextColor(Color.BLACK);
		}

		for (int jj = 0; jj < numberOfHeadings; jj++) {
			((TextView) findViewById(SECTION_HEADINGS + jj)).setTextColor(Color.BLACK);
		}
		
		for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
			((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww)).setTextColor(Color.BLACK);
		}
		
		for (int zz = 0; zz < numberOfCategoryItems; zz++) {
			((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz)).setTextColor(Color.BLACK);
		}
		
		for (int uu = 0; uu < numberOfTableTitles; uu++) {
			((TextView) findViewById(SECTION_TABLE_TITLES + uu)).setTextColor(Color.BLACK);
		}
		
		for (int ll = 0; ll < numberOfTables; ll++) {
			((TableLayout) findViewById(SECTION_TABLES + ll)).setBackgroundColor(Color.BLACK);
		}

		for (int tt = 0; tt < numberOfTableHeaders; tt++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_HEADERS + tt);
			cell.setTextColor(Color.BLACK);
			cell.setBackgroundColor(Color.LTGRAY);
		}
		
		for (int kk = 0; kk < numberOfTableContents; kk++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_CONTENTS + kk);
			cell.setTextColor(Color.BLACK);
			cell.setBackgroundColor(Color.LTGRAY);
		}
		
		for (int mm = 0; mm < numberOfImages; mm++) {
			((TextView) findViewById(SECTION_IMAGE_TEXTS + mm)).setTextColor(Color.BLACK);
		}
	}

	public void setNightMode() {
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("colourMode", "night");
		editor.commit();

		ScrollView scrollView = (ScrollView) findViewById(SCROLL_VIEW);
		scrollView.setBackgroundColor(Color.BLACK);
		
		((TextView) findViewById(ARTICLE_TITLE)).setTextColor(Color.WHITE);

		for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
			((TextView) findViewById(ARTICLE_INTROS + qq)).setTextColor(Color.WHITE);
		}
		
		for (int zz = 0; zz < numberOfTitles; zz++) {
			((TextView) findViewById(SECTION_TITLES + zz)).setTextColor(Color.WHITE);
		}

		for (int jj = 0; jj < numberOfHeadings; jj++) {
			((TextView) findViewById(SECTION_HEADINGS + jj)).setTextColor(Color.WHITE);
		}
		
		for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
			((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww)).setTextColor(Color.WHITE);
		}
		
		for (int zz = 0; zz < numberOfCategoryItems; zz++) {
			((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz)).setTextColor(Color.WHITE);
		}
		
		for (int uu = 0; uu < numberOfTableTitles; uu++) {
			((TextView) findViewById(SECTION_TABLE_TITLES + uu)).setTextColor(Color.WHITE);
		}
		
		for (int ll = 0; ll < numberOfTables; ll++) {
			((TableLayout) findViewById(SECTION_TABLES + ll)).setBackgroundColor(Color.WHITE);
		}

		for (int tt = 0; tt < numberOfTableHeaders; tt++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_HEADERS + tt);
			cell.setTextColor(Color.WHITE);
			cell.setBackgroundColor(Color.DKGRAY);
		}
		
		for (int kk = 0; kk < numberOfTableContents; kk++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_CONTENTS + kk);
			cell.setTextColor(Color.WHITE);
			cell.setBackgroundColor(Color.DKGRAY);
		}
		
		for (int mm = 0; mm < numberOfImages; mm++) {
			((TextView) findViewById(SECTION_IMAGE_TEXTS + mm)).setTextColor(Color.WHITE);
		}
	}
	
	public void setSepiaMode() {
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("colourMode", "sepia");
		editor.commit();

		ScrollView scrollView = (ScrollView) findViewById(SCROLL_VIEW);
		scrollView.setBackgroundColor(Color.rgb(247, 227, 192));
		
		((TextView) findViewById(ARTICLE_TITLE)).setTextColor(Color.rgb(95, 75, 50));

		for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
			((TextView) findViewById(ARTICLE_INTROS + qq)).setTextColor(Color.rgb(95, 75, 50));
		}
		
		for (int zz = 0; zz < numberOfTitles; zz++) {
			((TextView) findViewById(SECTION_TITLES + zz)).setTextColor(Color.rgb(95, 75, 50));
		}

		for (int jj = 0; jj < numberOfHeadings; jj++) {
			((TextView) findViewById(SECTION_HEADINGS + jj)).setTextColor(Color.rgb(95, 75, 50));
		}
		
		for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
			((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww)).setTextColor(Color.rgb(95, 75, 50));
		}
		
		for (int zz = 0; zz < numberOfCategoryItems; zz++) {
			((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz)).setTextColor(Color.rgb(95, 75, 50));
		}
		
		for (int uu = 0; uu < numberOfTableTitles; uu++) {
			((TextView) findViewById(SECTION_TABLE_TITLES + uu)).setTextColor(Color.rgb(95, 75, 50));
		}
		
		for (int ll = 0; ll < numberOfTables; ll++) {
			((TableLayout) findViewById(SECTION_TABLES + ll)).setBackgroundColor(Color.rgb(95, 75, 50));
		}

		for (int tt = 0; tt < numberOfTableHeaders; tt++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_HEADERS + tt);
			cell.setTextColor(Color.BLACK);
			cell.setBackgroundColor(Color.LTGRAY);
		}
		
		for (int kk = 0; kk < numberOfTableContents; kk++) {
			TextView cell = (TextView) findViewById(SECTION_TABLE_CONTENTS + kk);
			cell.setTextColor(Color.BLACK);
			cell.setBackgroundColor(Color.LTGRAY);
		}
		
		for (int mm = 0; mm < numberOfImages; mm++) {
			((TextView) findViewById(SECTION_IMAGE_TEXTS + mm)).setTextColor(Color.rgb(95, 75, 50));
		}
	}
	
	public void increaseTextSize(float increaseTextBy) {
		if (increaseTextBy < 0) {
			increaseTextBy = increaseTextBy * -1;
		}
		
		if (increaseTextBy != 0) {
			increaseTextBy = increaseTextBy * DEFAULT_CHANGE_TEXT_SIZE_BY;
		
			TextView articleTitle = ((TextView) findViewById(ARTICLE_TITLE));
			float titleTextSize = articleTitle.getTextSize();
			if ((titleTextSize + DEFAULT_CHANGE_TEXT_SIZE_BY) < MAXIMUM_TEXT_SIZE) {
				articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleTextSize + increaseTextBy);
				
				for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
					TextView textView = ((TextView) findViewById(ARTICLE_INTROS + qq));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int zz = 0; zz < numberOfTitles; zz++) {
					TextView textView = ((TextView) findViewById(SECTION_TITLES + zz));
					float textSize = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize + increaseTextBy);
					
					Button button = ((Button) findViewById(SECTION_BUTTONS + zz));
					float buttonSize = button.getTextSize();
					button.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonSize + increaseTextBy);
				}
				
				for (int jj = 0; jj < numberOfHeadings; jj++) {
					TextView textView = ((TextView) findViewById(SECTION_HEADINGS + jj));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
					TextView textView = ((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int zz = 0; zz < numberOfCategoryItems; zz++) {
					TextView textView = ((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int uu = 0; uu < numberOfTableTitles; uu++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_TITLES + uu));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int tt = 0; tt < numberOfTableHeaders; tt++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_HEADERS + tt));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int kk = 0; kk < numberOfTableContents; kk++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_CONTENTS + kk));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
				
				for (int kk = 0; kk < numberOfImages; kk++) {
					TextView textView = ((TextView) findViewById(SECTION_IMAGE_TEXTS + kk));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size + increaseTextBy);
				}
			} else {
				Toast.makeText(this, "The text is as big as possible", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void decreaseTextSize(float decreaseTextBy) {
		if (decreaseTextBy < 0) {
			decreaseTextBy = decreaseTextBy * -1;
		}
		
		if (decreaseTextBy != 0) {
			decreaseTextBy = decreaseTextBy * DEFAULT_CHANGE_TEXT_SIZE_BY;
		
			TextView articleTitle = ((TextView) findViewById(ARTICLE_TITLE));
			float titleTextSize = articleTitle.getTextSize();
			if ((titleTextSize - DEFAULT_CHANGE_TEXT_SIZE_BY) > MINIMUM_TEXT_SIZE) {
				articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleTextSize - decreaseTextBy);

				for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
					TextView textView = ((TextView) findViewById(ARTICLE_INTROS + qq));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int zz = 0; zz < numberOfTitles; zz++) {
					TextView textView = ((TextView) findViewById(SECTION_TITLES + zz));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
					
					Button button = ((Button) findViewById(SECTION_BUTTONS + zz));
					float buttonSize = button.getTextSize();
					button.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonSize - decreaseTextBy);
				}
				
				for (int jj = 0; jj < numberOfHeadings; jj++) {
					TextView textView = ((TextView) findViewById(SECTION_HEADINGS + jj));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
					TextView textView = ((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int zz = 0; zz < numberOfCategoryItems; zz++) {
					TextView textView = ((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int uu = 0; uu < numberOfTableTitles; uu++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_TITLES + uu));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int tt = 0; tt < numberOfTableHeaders; tt++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_HEADERS + tt));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int kk = 0; kk < numberOfTableContents; kk++) {
					TextView textView = ((TextView) findViewById(SECTION_TABLE_CONTENTS + kk));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
				
				for (int kk = 0; kk < numberOfImages; kk++) {
					TextView textView = ((TextView) findViewById(SECTION_IMAGE_TEXTS + kk));
					float size = textView.getTextSize();
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size - decreaseTextBy);
				}
			} else {
				Toast.makeText(this, "The text is as small as possible", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void setDefaultTextSize() {
		TextView articleTitle = ((TextView) findViewById(ARTICLE_TITLE));
		articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_ARTICLE_TITLE_SIZE);
			
		for (int qq = 0; qq < numberOfIntroParagraphs; qq++) {
			TextView textView = ((TextView) findViewById(ARTICLE_INTROS + qq));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
				
		for (int zz = 0; zz < numberOfTitles; zz++) {
			TextView textView = ((TextView) findViewById(SECTION_TITLES + zz));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_HEADING_SIZE);
			
			Button button = ((Button) findViewById(SECTION_BUTTONS + zz));
			button.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
				
		for (int jj = 0; jj < numberOfHeadings; jj++) {
			TextView textView = ((TextView) findViewById(SECTION_HEADINGS + jj));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_HEADING_SIZE);
		}
		
		for (int ww = 0; ww < numberOfCategoryParagraphs; ww++) {
			TextView textView = ((TextView) findViewById(SECTION_CATEGORY_PARAGRAPHS + ww));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
		
		for (int zz = 0; zz < numberOfCategoryItems; zz++) {
			TextView textView = ((TextView) findViewById(SECTION_CATEGORY_ITEMS + zz));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
		
		for (int uu = 0; uu < numberOfTableTitles; uu++) {
			TextView textView = ((TextView) findViewById(SECTION_TABLE_TITLES + uu));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_HEADING_SIZE);
		}
				
		for (int tt = 0; tt < numberOfTableHeaders; tt++) {
			TextView textView = ((TextView) findViewById(SECTION_TABLE_HEADERS + tt));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_HEADING_SIZE);
		}
			
		for (int kk = 0; kk < numberOfTableContents; kk++) {
			TextView textView = ((TextView) findViewById(SECTION_TABLE_CONTENTS + kk));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
		
		for (int kk = 0; kk < numberOfImages; kk++) {
			TextView textView = ((TextView) findViewById(SECTION_IMAGE_TEXTS + kk));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_TEXT_SIZE);
		}
	}
}