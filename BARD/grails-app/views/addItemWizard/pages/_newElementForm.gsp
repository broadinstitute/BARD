<%@ page import="bard.db.registration.*" %>

<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">
			<div class="span1">
				<label>Label: </label>			
			</div>    	
	    	<div class="span5">	    			
				<input class="input-large" type="text" size='20' id="label" name='label'  value="">
	    	</div>
	    	<div class="span2">
	    		<label>Abbreviation: </label>
	    	</div>
	    	<div class="span4">
				<input class="input-large" type="text" size='10' id="abbreviation" name='abbreviation'  value="">
	    	</div>	    			
	    </div>
	</div>		
</div>
<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">	    	
	    	<div class="span2">	    			
		    	<label>Unit:</label>
	    	</div>
	    	<div class="span10">
    			<input type="hidden" id="valueUnitId" name="valueUnitId">
	    	</div>	    			
	    </div>
	</div>		
</div>
<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">	    	
	    	<div class="span2">	    			
		    	<label>Description:</label>
	    	</div>
	    	<div class="span10">
	    		<textarea id="description" name="description" rows="4" cols="100" maxlength="1000"></textarea>
	    	</div>	    			
	    </div>
	</div>		
</div>
<div class="row-fluid">
	<div class="span12">
		<div class="row-fluid">	    	
	    	<div class="span2">	    			
		    	<label>Synonyms:</label>
	    	</div>
	    	<div class="span10">
	    		<textarea id="synonyms" name="synonyms" rows="4" cols="100" maxlength="1000"></textarea>
	    	</div>	    			
	    </div>
	</div>		
</div>