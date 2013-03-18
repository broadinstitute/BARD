package bard.dm.cars.spreadsheet

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 3/15/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectIdsToLoad {

    private Set<Integer> projectIdsToLoadSet

    public ProjectIdsToLoad() {
        projectIdsToLoadSet = new HashSet<Integer>()
    }

    public void loadProjectIds(String filePath, int numHeaderRows) {
        BufferedReader reader = new BufferedReader(new FileReader(filePath))

        int lineNum = 0;
        while (lineNum < numHeaderRows && reader.readLine() != null) {
            lineNum++
        }

        String curLine;
        while ((curLine = reader.readLine()) != null) {
            projectIdsToLoadSet.add(Integer.valueOf(curLine))
        }
        reader.close()
    }

    public boolean contains(Integer projectId) {
        return projectIdsToLoadSet.contains(projectId)
    }
}
