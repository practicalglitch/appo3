package com.practicalglitch.ao3reader;

import org.apio3.Types.Work;
import org.apio3.Types.WorkChapter;

import java.util.HashMap;
import java.util.Objects;

// first person who complains this should be inherited from work gets a tickle from the tickle monster
public class SavedWork {
	
	public Work Work;
	public HashMap<String, Float> ReadStatus = new HashMap<>();
	
	public boolean CachedInfoOnly = false;
	
	
	public int IndexOf(WorkChapter chapter) {
		for (int i = 0; i < Work.Contents.length; i++)
			if (Objects.equals(Work.Contents[i].ChapterID, chapter.ChapterID))
				return i;
		return -1;
	}
	
	public int UnreadChapters() {
		
		if (Work.Contents == null)
			return Work.ChaptersAvailable;
		
		int unreadChapters = 0;
		
		for (float readStatus : ReadStatus.values())
			if (readStatus != 100)
				unreadChapters++;
		
		return unreadChapters;
	}
	
	public static SavedWork DummySavedWork() {
		Work work = new Work();
		work.Id = "421312";
		work.Title = "Amazing Work";
		work.Author = "AwesomeMan";
		work.AuthorLoc = "/users/AwesomeMan/pseuds/AwesomeMan";
		work.Rating = "General Audiences";
		work.Category = new String[]{"Gen"};
		work.Warning = "Creator Chose Not To Use Archive Warnings";
		work.Finished = false;
		work.Relationships = new String[]{"Mr. Awesome & Everyone"};
		work.Characters = new String[]{"Mr. Awesome", "Mr. Awesome's Second In Command"};
		work.Freeforms = new String[]{"Awesome", "Cool", "Amazing", "Fun", "I dont know what i'm doing but this is AWESOME"};
		work.Language = "English";
		work.Fandoms = new String[]{"Awesome Cinematic Universe"};
		work.Summary = "<p>Mr. Awesome's Back!</p>";
		work.ChaptersTotal = 5;
		work.ChaptersAvailable = 3;
		work.Words = 92033;
		work.Comments = 2;
		work.Kudos = 6;
		work.Bookmarks = 1;
		work.Hits = 68;
		work.DatePublished = "2020-3-5";
		work.DateUpdated = "2022-6-7";
		
		work.Contents = new WorkChapter[3];
		
		work.Contents[0] = new WorkChapter();
		work.Contents[1] = new WorkChapter();
		work.Contents[2] = new WorkChapter();
		
		work.Contents[0].Title = "Mr. Awesome Blasts Off";
		work.Contents[1].Title = "Mr. Awesome Does Stuff";
		work.Contents[2].Title = "Mr. Awesome fucking dies";
		
		work.Contents[0].ChapterIndex = 1;
		work.Contents[1].ChapterIndex = 2;
		work.Contents[2].ChapterIndex = 3;
		
		work.Contents[0].UploadDate = "2020-3-5";
		work.Contents[1].UploadDate = "2021-1-2";
		work.Contents[2].UploadDate = "2022-6-7";
		
		SavedWork sWork = new SavedWork();
		sWork.Work = work;
		//sWork.ChapterReadStatus = new float[]{100, 0, 0};
		
		return sWork;
	}
	
	private String buildList(String[] list, int maxSize, String delim) {
		if (maxSize < 0)
			maxSize = 9999999;
		StringBuilder builtString = new StringBuilder();
		if(list.length > 0)
			builtString.append(list[0]);
		for (int i = 1; i < Math.min(list.length, maxSize); i++) {
			builtString.append(delim).append(list[i]);
		}
		if (list.length > maxSize)
			builtString.append(delim).append("+").append(list.length - maxSize);
		return builtString.toString();
	}

	public String FandomList(int maxSize) {
		return buildList(Work.Fandoms, maxSize, ", ");
	}

	public String FandomList(int maxSize, String delim) {
		return buildList(Work.Fandoms, maxSize, delim);
	}
	
	public String RelationshipList(int maxSize) {
		return buildList(Work.Relationships, maxSize, ", ");
	}
	
	public String CharacterList(int maxSize) {
		return buildList(Work.Characters, maxSize, ", ");
	}
	
	public String FreeformList(int maxSize) {
		return buildList(Work.Freeforms, maxSize, ", ");
	}
}
