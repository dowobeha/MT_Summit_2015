// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

import scala.collection.Map
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SortedSet
import scala.collection.mutable.TreeSet

class Split(source:String, target:String, charPointsString:String) {

//  println("Hello")
  
//  val source="Sie fragen: Will die Regierung in Berlin uns erniedrigen?"
//  val target="You ask: us wants to humiliate the Government in Berlin?"
//  val charPointsString="0:2-0:2 4:9-4:6 10:10-7:7 12:15-12:19 17:19-31:33 21:29-35:44 31:32-46:47 34:39-49:54 41:43-9:10 45:55-21:29 56:56-55:55"

//  def split(source:String, target:String, charPointsString:String) : (Seq[String], Map[String,Int], Seq[String], Map[String,Int]) = {
//  println(source)
//  println(target)
//  println()
	private val charPoints=charPointsString.split("\\s+")
  private val sourceCharPoints = charPoints.map( p => p.split("-")(0) )
  private val targetCharPoints = charPoints.map( p => p.split("-")(1) )
  
  private val sourceCharStartPoints = sourceCharPoints.map ( p => p.split(":")(0).toInt )
  private val sourceCharEndPoints   = sourceCharPoints.map ( p => p.split(":")(1).toInt )
  
  private val targetCharStartPoints = targetCharPoints.map ( p => p.split(":")(0).toInt )
  private val targetCharEndPoints   = targetCharPoints.map ( p => p.split(":")(1).toInt )
  
  private val sourceBoundaryPoints:SortedSet[Int] = new TreeSet[Int]
  private val targetBoundaryPoints:SortedSet[Int] = new TreeSet[Int]
  
	sourceBoundaryPoints.add(0)
  targetBoundaryPoints.add(0)
  
  sourceBoundaryPoints.add(source.length)
  targetBoundaryPoints.add(target.length)

//  private def spaceIndices(string:String) : SortedSet[Int] = {
//    
//	  val set = new TreeSet[Int]
//    
//		var index = string.indexOf(" ")
//    
//		while (index > 0) {
//			set.add(index)
//			index = string.indexOf(" ", index+1)
//		}
//    
//	  return set
//    
//  }
//
//  spaceIndices(source).foreach( i => {
//	  sourceBoundaryPoints.add(i)
//	  sourceBoundaryPoints.add(i+1)
//  })
//  
//  spaceIndices(target).foreach( i => {
//	  targetBoundaryPoints.add(i)
//	  targetBoundaryPoints.add(i+1)
//  })
  
  sourceCharStartPoints.foreach( i => sourceBoundaryPoints.add(i)   )
  sourceCharEndPoints.foreach(   i => sourceBoundaryPoints.add(i+1) )
  
  targetCharStartPoints.foreach( i => targetBoundaryPoints.add(i)   )
  targetCharEndPoints.foreach(   i => targetBoundaryPoints.add(i+1) )
  
  private def splitAtBoundaries(string:String, boundaryPoints:SortedSet[Int]) : (Seq[String],Map[String,Int]) = {
    val tokens = new ArrayBuffer[String]
    val map = new scala.collection.mutable.HashMap[String,Int]
    
    var from=0
    var to=0
    var wordIndex=0
    
    boundaryPoints.foreach( i => {
      to=i
      if (to > from) {
        val key = s"${from}:${to-1}"
//        println(s"${key} => ${wordIndex}")
        map(key)=wordIndex
        tokens.append(string.substring(from, to))
        wordIndex += 1
      }
      from=i
    })
    
    return (tokens,map)
  }

//  println(sourceBoundaryPoints)
//  println(targetBoundaryPoints)
  
//  println("Source:")
  val (sourceTokens, sourceIndexMap) = splitAtBoundaries(source, sourceBoundaryPoints)
//  println("Target:")
  val (targetTokens,targetIndexMap) = splitAtBoundaries(target, targetBoundaryPoints)
  
  def getSourceTokens() : Seq[String] = {
    return sourceTokens
  }

  def getTargetTokens() : Seq[String] = {
    return targetTokens
  }
  
  private val wordAlignmentPoints = new ArrayBuffer[String]
  
  charPoints.foreach( { p => 
    try {
//    println(p)
    val parts = p.split("-")

    val sourceCharPoints = parts(0); //println("\tsource")
    val sourceWordIndex  = sourceIndexMap(sourceCharPoints)
    
    val targetCharPoints = parts(1); //println("\ttarget")
    val targetWordIndex  = targetIndexMap(targetCharPoints)
    
    wordAlignmentPoints.append(s"${sourceWordIndex}-${targetWordIndex}")
    } catch {
      case e:NoSuchElementException => {
        // This space intentionally left Blank
      }
    }
    
  })
  
  
//  def getSourceIndexMap() : Map[String,Int] = {
//    return sourceIndexMap
//  }
//
//  def getTargetIndexMap() : Map[String,Int] = {
//    return targetIndexMap
//  }
  
  def getWordAlignments() : Seq[String] = {
    return wordAlignmentPoints
  }
  
//  
//  println(sourceTokens)
//println(sourceIndexMap)
//
//
//  println(targetTokens)
//  println(targetIndexMap)
  
  
//  return (sourceTokens, sourceIndexMap, targetTokens, targetIndexMap)
  
//  }

  
//  split(source, target, charPointsString)

  
}
