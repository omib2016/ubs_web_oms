package com.ubs.entity;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by omib on 16/09/2017.
 */
public class PriceComparator implements Comparator<BigDecimal>
{
	
	public static final PriceComparator ASC = new PriceComparator(true);
	public static final PriceComparator DESC = new PriceComparator(false);
	
	private final int sign;		

	public PriceComparator(boolean ascending) 
	{	
		this.sign = ascending ? 1 : -1;
	}


	@Override
	public int compare(BigDecimal o1, BigDecimal o2) 
	{
		if ( o1 == o2)
		{
			return 0;
		}
		
		return o1.compareTo(o2) * sign;
	}

}
